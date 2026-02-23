---
---
const copySbomCodeHtml = `
<span class="sbom-copy-header">
    <button class="sbom-copy-button">
        <span class="sbom-copy-text" style="display: none;">Copied!</span>
        <img class="copy-icon" src="assets/images/copy.png" alt="copy"/>
    </button>
</span>`;

(async () => {
    fetch('https://api.github.com/repos/giulong/spectrum/contents/docs/sboms')
        .then(response => response.json())
        .then(json => json.sort(compareSemanticVersions))
        .then(json => {
            const latestJson = json.pop();
            const rows = json
                .map(sbom => {
                    const xmlUrl = "{{ site.docs_url }}/{{ site.sboms_endpoint }}" + sbom.name + "/sbom.xml";
                    const xmlVersionColumn = '<td><a href="' + xmlUrl + '" target="_blank">' + sbom.name + '</a></td>';
                    const xmlFullPathColumn = '<td>' + xmlUrl + '</td>';

                    const jsonUrl = "{{ site.docs_url }}/{{ site.sboms_endpoint }}" + sbom.name + "/sbom.json";
                    const jsonVersionColumn = '<td><a href="' + jsonUrl + '" target="_blank">' + sbom.name + '</a></td>';
                    const jsonFullPathColumn = '<td>' + jsonUrl + '</td>';

                    const copyCodeColumn = '<td>' + copySbomCodeHtml + '</td>';

                    return xmlVersionColumn + xmlFullPathColumn + copyCodeColumn + '</tr><tr>' + jsonVersionColumn + jsonFullPathColumn + copyCodeColumn;
                })
                .reverse()
                .join('</tr><tr>');

            const latestXmlUrl = "{{ site.docs_url }}/{{ site.sboms_endpoint }}" + latestJson.name + "/sbom.xml";
            document.getElementById('latest-sbom-xml-version').innerHTML = '<a href="' + latestXmlUrl + '" target="_blank">' + latestJson.name + '</a>';
            document.getElementById('latest-sbom-xml-path').innerText = latestXmlUrl;
            document.getElementById('latest-sbom-xml-copy-header').innerHTML = copySbomCodeHtml;

            const latestJsonUrl = "{{ site.docs_url }}/{{ site.sboms_endpoint }}" + latestJson.name + "/sbom.json";
            document.getElementById('latest-sbom-json-version').innerHTML = '<a href="' + latestJsonUrl + '" target="_blank">' + latestJson.name + '</a>';
            document.getElementById('latest-sbom-json-path').innerText = latestJsonUrl;
            document.getElementById('latest-sbom-json-copy-header').innerHTML = copySbomCodeHtml;

            document.getElementById('sboms-body').innerHTML = '<tr>' + rows + '</tr>';
        })
        .catch(error => {
            document.getElementById('latest-sbom-xml-version').innerText = 'N/A';
            document.getElementById('latest-sbom-xml-path').innerText = '{{ site.docs_url }}/{{ site.sboms_endpoint }}<VERSION>/sbom.xml';
            document.getElementById('latest-sbom-xml-copy-header').innerHTML = copySbomCodeHtml;

            document.getElementById('latest-sbom-json-version').innerText = 'N/A';
            document.getElementById('latest-sbom-json-path').innerText = '{{ site.docs_url }}/{{ site.sboms_endpoint }}<VERSION>/sbom.json';
            document.getElementById('latest-sbom-json-copy-header').innerHTML = copySbomCodeHtml;
        })
        .finally(() => {
            const sbomHeaders = document.querySelectorAll('.sbom-copy-header');
            const sbomButtons = document.querySelectorAll('.sbom-copy-button');
            sbomButtons.forEach((copyButton, index) => {
                try {
                    const url = sbomHeaders[index].parentElement.previousElementSibling.textContent;
                    copyButton.addEventListener('click', () => {
                        window.navigator.clipboard.writeText(url);
                        const copyText = copyButton.querySelector('.sbom-copy-text');
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
        });
})()
