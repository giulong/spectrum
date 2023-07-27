package io.github.giulong.spectrum.internals.jackson.json_schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JsonSubTypesResolver;
import io.github.giulong.spectrum.interfaces.JsonSchemaTypes;
import io.github.giulong.spectrum.internals.jackson.views.Views;
import io.github.giulong.spectrum.pojos.Configuration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.victools.jsonschema.generator.Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT;
import static com.github.victools.jsonschema.generator.Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES;
import static com.github.victools.jsonschema.module.jackson.JacksonOption.SKIP_SUBTYPE_LOOKUP;
import static java.util.stream.Collectors.toList;

@Slf4j
public class JsonSchemaGeneratorModule implements Module {

    @SneakyThrows
    @Override
    public void applyToConfigBuilder(final SchemaGeneratorConfigBuilder schemaGeneratorConfigBuilder) {
        schemaGeneratorConfigBuilder
                .with(new JacksonModule(SKIP_SUBTYPE_LOOKUP))
                .with(FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT)
                .with(MAP_VALUES_AS_ADDITIONAL_PROPERTIES);

        schemaGeneratorConfigBuilder
                .forFields()
                .withIgnoreCheck(this::jsonViewInternalPredicate)
                .withTargetTypeOverridesResolver(this::multipleTypesResolver);

        schemaGeneratorConfigBuilder
                .forTypesInGeneral()
                .withSubtypeResolver(new PublicSubTypeResolver());

        final String jsonSchema = new SchemaGenerator(schemaGeneratorConfigBuilder.build()).generateSchema(Configuration.class).toString();
        final URI targetFolderUri = JsonSchemaGeneratorModule.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        final String targetFolder = Path.of(targetFolderUri).getParent().toString();
        final Path jsonSchemaPath = Path.of(targetFolder, "json-schemas", "Configuration.json");

        //noinspection ResultOfMethodCallIgnored
        jsonSchemaPath.getParent().toFile().mkdirs();
        Files.writeString(jsonSchemaPath, jsonSchema);
    }

    private static boolean isInternal(final AnnotatedElement annotatedElement) {
        return annotatedElement.isAnnotationPresent(JsonView.class) && Arrays.asList(annotatedElement.getAnnotation(JsonView.class).value()).contains(Views.Internal.class);
    }

    private List<ResolvedType> multipleTypesResolver(final FieldScope field) {
        final JsonSchemaTypes jsonSchemaTypes = field.getAnnotationConsideringFieldAndGetterIfSupported(JsonSchemaTypes.class);

        if (jsonSchemaTypes == null) {
            return null;
        }

        return Arrays
                .stream(jsonSchemaTypes.value())
                .map(c -> field.getContext().resolve(c))
                .toList();
    }

    private boolean jsonViewInternalPredicate(final FieldScope fieldScope) {
        final Field f = fieldScope.getRawMember();
        log.debug("Checking if field '{}' is internal", f.getName());

        return isInternal(f);
    }

    private static class PublicSubTypeResolver extends JsonSubTypesResolver {

        public List<ResolvedType> lookUpSubtypesFromAnnotation(final ResolvedType declaredType, final JsonSubTypes subtypesAnnotation, final TypeContext context) {
            return Optional
                    .ofNullable(super.lookUpSubtypesFromAnnotation(declaredType, subtypesAnnotation, context))
                    .orElse(List.of())
                    .stream()
                    .filter(subtype -> !isInternal(subtype.getErasedType()))
                    .peek(subtype -> log.info("Polymorphic type '{}' is public", subtype.getErasedType().getSimpleName()))
                    .collect(toList());
        }
    }
}
