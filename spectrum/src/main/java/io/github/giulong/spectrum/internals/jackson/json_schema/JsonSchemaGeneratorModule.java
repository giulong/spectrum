package io.github.giulong.spectrum.internals.jackson.json_schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.victools.jsonschema.generator.FieldScope;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.TypeContext;
import com.github.victools.jsonschema.module.jackson.JsonSubTypesResolver;
import io.github.giulong.spectrum.internals.jackson.views.Views;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
public class JsonSchemaGeneratorModule extends JsonSchemaInternalGeneratorModule {

    @Override
    public void applyToConfigBuilder(final SchemaGeneratorConfigBuilder schemaGeneratorConfigBuilder) {
        super.applyToConfigBuilder(schemaGeneratorConfigBuilder);

        schemaGeneratorConfigBuilder
                .forFields()
                .withIgnoreCheck(this::jsonViewInternalPredicate);

        schemaGeneratorConfigBuilder
                .forTypesInGeneral()
                .withSubtypeResolver(new PublicSubTypeResolver());

        writeSchema(schemaGeneratorConfigBuilder, "Configuration.json");
    }

    private static boolean isInternal(final AnnotatedElement annotatedElement) {
        return annotatedElement.isAnnotationPresent(JsonView.class) && Arrays.asList(annotatedElement.getAnnotation(JsonView.class).value()).contains(Views.Internal.class);
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
