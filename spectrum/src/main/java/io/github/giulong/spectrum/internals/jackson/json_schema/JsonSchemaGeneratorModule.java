package io.github.giulong.spectrum.internals.jackson.json_schema;

import static java.util.function.Predicate.not;

import java.io.FileReader;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.victools.jsonschema.generator.FieldScope;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.TypeContext;
import com.github.victools.jsonschema.module.jackson.JsonSubTypesResolver;

import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

@Slf4j
public class JsonSchemaGeneratorModule extends JsonSchemaInternalGeneratorModule {

    @SneakyThrows
    @Override
    public void applyToConfigBuilder(final SchemaGeneratorConfigBuilder schemaGeneratorConfigBuilder) {
        commonSetupFor(schemaGeneratorConfigBuilder)
                .forFields()
                .withIgnoreCheck(this::jsonViewInternalPredicate);

        schemaGeneratorConfigBuilder
                .forTypesInGeneral()
                .withSubtypeResolver(new PublicSubTypeResolver());

        final Model model = new MavenXpp3Reader().read(new FileReader("pom.xml"));
        final String version = model.getProperties().getProperty("revision");
        final String schemaName = "Configuration-schema.json";
        final Path jsonSchemaPath = Path.of(System.getProperty("user.dir"), "docs", "json-schemas", version, schemaName);

        writeSchema(schemaGeneratorConfigBuilder, getTargetJsonSchemaFolder().resolve(schemaName));

        if (Files.exists(jsonSchemaPath)) {
            log.warn("Trying to override the json schema for version {}. Need to bump Spectrum's version first! Skipping schema generation...", version);
            return;
        }

        writeSchema(schemaGeneratorConfigBuilder, jsonSchemaPath);
    }

    private static boolean isInternal(final AnnotatedElement annotatedElement) {
        return annotatedElement.isAnnotationPresent(JsonView.class) && Arrays.asList(annotatedElement.getAnnotation(JsonView.class).value()).contains(Internal.class);
    }

    private boolean jsonViewInternalPredicate(final FieldScope fieldScope) {
        final Field f = fieldScope.getRawMember();
        log.debug("Checking if field '{}' is internal", f.getName());

        return isInternal(f);
    }

    private static final class PublicSubTypeResolver extends JsonSubTypesResolver {
        public List<ResolvedType> lookUpSubtypesFromAnnotation(final ResolvedType declaredType, final JsonSubTypes subtypesAnnotation, final TypeContext context) {
            return Optional
                    .ofNullable(super.lookUpSubtypesFromAnnotation(declaredType, subtypesAnnotation, context))
                    .orElse(List.of())
                    .stream()
                    .filter(not(subtype -> isInternal(subtype.getErasedType())))
                    .peek(subtype -> log.info("Polymorphic type '{}' is public", subtype.getErasedType().getSimpleName()))
                    .toList();
        }
    }
}
