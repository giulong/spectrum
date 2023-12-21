const tocList = document.getElementById('toc-list');
const tocElements = Array.from(document.querySelectorAll('h1, h2, h3, h4'));

document.onload = leftMenu();

function leftMenu() {
    tocElements
        .map(tocElement => {
            const li = document.createElement('li');
            const text = tocElement.innerText.toLowerCase();

            li.setAttribute('onclick', 'navigateTo("' + text.replaceAll(' ', '-') + '")');
            li.classList.add('search-result', tocElement.nodeName.toLowerCase());
            li.innerText = text;

            return li;
        })
        .forEach(li => tocList.appendChild(li));
}
