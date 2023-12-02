package io.github.giulong.spectrum.internals.jackson.json_schema;

import com.fasterxml.classmate.ResolvedType;
import com.github.victools.jsonschema.generator.FieldScope;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JsonSubTypesResolver;
import io.github.giulong.spectrum.interfaces.JsonSchemaTypes;
import io.github.giulong.spectrum.pojos.Configuration;
import lombok.SneakyThrows;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static com.github.victools.jsonschema.generator.Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT;
import static com.github.victools.jsonschema.generator.Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES;
import static com.github.victools.jsonschema.module.jackson.JacksonOption.FLATTENED_ENUMS_FROM_JSONVALUE;
import static com.github.victools.jsonschema.module.jackson.JacksonOption.SKIP_SUBTYPE_LOOKUP;

public class JsonSchemaInternalGeneratorModule implements Module {

    @Override
    public void applyToConfigBuilder(final SchemaGeneratorConfigBuilder schemaGeneratorConfigBuilder) {
        schemaGeneratorConfigBuilder
                .with(new JacksonModule(SKIP_SUBTYPE_LOOKUP, FLATTENED_ENUMS_FROM_JSONVALUE))
                .with(FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT, MAP_VALUES_AS_ADDITIONAL_PROPERTIES);

        schemaGeneratorConfigBuilder
                .forFields()
                .withEnumResolver(this::enumValuesResolver)
                .withTargetTypeOverridesResolver(this::multipleTypesResolver);

        schemaGeneratorConfigBuilder
                .forTypesInGeneral()
                .withSubtypeResolver(new JsonSubTypesResolver());

        writeSchema(schemaGeneratorConfigBuilder, "ConfigurationInternal.json");
    }

    @SneakyThrows
    protected void writeSchema(final SchemaGeneratorConfigBuilder schemaGeneratorConfigBuilder, final String name) {
        final String jsonSchema = new SchemaGenerator(schemaGeneratorConfigBuilder.build()).generateSchema(Configuration.class).toString();
        final URI targetFolderUri = JsonSchemaInternalGeneratorModule.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        final String targetFolder = Path.of(targetFolderUri).getParent().toString();
        final Path jsonSchemaPath = Path.of(targetFolder, "json-schemas", name);

        //noinspection ResultOfMethodCallIgnored
        jsonSchemaPath.getParent().toFile().mkdirs();
        Files.writeString(jsonSchemaPath, jsonSchema);
    }

    private List<String> enumValuesResolver(final FieldScope field) {
        final JsonSchemaTypes jsonSchemaTypes = field.getAnnotationConsideringFieldAndGetterIfSupported(JsonSchemaTypes.class);

        if (jsonSchemaTypes == null || jsonSchemaTypes.valueList().length == 0) {
            return null;
        }

        return Arrays
                .stream(jsonSchemaTypes.valueList())
                .toList();
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
}
