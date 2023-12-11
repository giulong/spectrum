---
---
(async () => {
    const jsonSchemasBody = document.getElementById('json-schemas-body');
    const response = await fetch('https://api.github.com/repos/giulong/spectrum/contents/docs/json-schemas');
    const json = await response.json();
    const rows = json
        .map(jsonSchema => {
            const url = "{{ site.docs_url }}/{{ site.json_schemas_endpoint }}" + jsonSchema.name + "/Configuration-schema.json";
            const versionColumn = '<td><a href="' + url + '" target="_blank">' + jsonSchema.name + '</a></td>';
            const fullPathColumn = '<td>' + url + '</td>';

            return versionColumn + fullPathColumn;
        })
        .join('</tr><tr>');

    jsonSchemasBody.innerHTML = '<tr>' + rows + '</tr>';
})()
