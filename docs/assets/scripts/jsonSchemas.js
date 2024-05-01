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

const copyCodeHtml = `
<td>
    <span class="schema-copy-header">
        <button class="schema-copy-button">
            <span class="schema-copy-text" style="display: none;">Copied!</span>
            <img class="copy-icon" src="assets/images/copy.png" alt="copy"/>
        </button>
    </span>
</td>`;

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

            return versionColumn + fullPathColumn + copyCodeHtml;
        })
        .reverse()
        .join('</tr><tr>');

    const latestUrl = "{{ site.docs_url }}/{{ site.json_schemas_endpoint }}" + latestJson.name + "/Configuration-schema.json";
    document.getElementById('latest-json-schema-version').innerHTML = '<a href="' + latestUrl + '" target="_blank">' + latestJson.name + '</a>';
    document.getElementById('latest-json-schema-path').innerHTML = latestUrl;

    jsonSchemasBody.innerHTML = '<tr>' + rows + '</tr>';

    const jsonSchemaHeaders = document.querySelectorAll('.schema-copy-header');
    const jsonSchemaButtons = document.querySelectorAll('.schema-copy-button');
    jsonSchemaButtons.forEach((copyButton, index) => {
        try {
            const url = jsonSchemaHeaders[index].parentElement.previousElementSibling.innerText;

            copyButton.addEventListener('click', () => {
                window.navigator.clipboard.writeText(url);
                const copyText = copyButton.querySelector('.schema-copy-text');
                const copyIcon = copyButton.querySelector('.copy-icon');

                copyText.style.display = 'inline';
                copyIcon.src = 'assets/images/check.png';

                setTimeout(() => {
                    copyText.style.display = 'none';
                    copyIcon.src = 'assets/images/copy.png';
                }, 2000);
            });
        } catch (error) {
            console.error(error);
        }
    });
})()
