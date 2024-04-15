---
---
function compareSemanticVersions(a, b) {
    const aTokens = a['name'].split('.');
    const bTokens = b['name'].split('.');

    for (i = 0; i < 3; i++) {
        const aToken = parseInt(aTokens[i]);
        const bToken = parseInt(bTokens[i]);

        if (aToken !== bToken) {
            return aToken > bToken ? 1 : -1;
        }
    }

    return bTokens.length - aTokens.length;
}

(async () => {
    const jsonSchemasBody = document.getElementById('json-schemas-body');
    const response = await fetch('https://api.github.com/repos/giulong/spectrum/contents/docs/json-schemas');
    const json = await response.json();
    const sortedJson = json.sort(compareSemanticVersions);
    const latestJson = sortedJson.pop();
    const rows = sortedJson
        .map(jsonSchema => {
            const url = "{{ site.docs_url }}/{{ site.json_schemas_endpoint }}" + jsonSchema.name + "/Configuration-schema.json";
            const versionColumn = '<td><a href="' + url + '" target="_blank">' + jsonSchema.name + '</a></td>';
            const fullPathColumn = '<td>' + url + '</td>';

            return versionColumn + fullPathColumn;
        })
        .reverse()
        .join('</tr><tr>');

    const latestUrl = "{{ site.docs_url }}/{{ site.json_schemas_endpoint }}" + latestJson.name + "/Configuration-schema.json";
    document.getElementById('latest-json-schema-version').innerHTML = '<a href="' + latestUrl + '" target="_blank">' + latestJson.name + '</a>';
    document.getElementById('latest-json-schema-path').innerHTML = latestUrl;

    jsonSchemasBody.innerHTML = '<tr>' + rows + '</tr>';
})()
