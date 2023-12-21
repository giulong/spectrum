const leftMenu = document.getElementById('left-menu');
const tocList = document.getElementById('toc-list');
const tocElements = Array.from(document.querySelectorAll('h1, h2, h3, h4'));
const toggleTocButton = document.getElementById('toggle-toc-button');

document.onload = buildLeftMenu();

function buildLeftMenu() {
    tocElements
        .map(tocElement => {
            const li = document.createElement('li');
            const text = tocElement.innerText.toLowerCase();

            li.setAttribute('onclick', 'navigateTo("' + text.replaceAll(' ', '-') + '")');
            li.classList.add('toc-element', tocElement.nodeName.toLowerCase());
            li.innerText = text;

            return li;
        })
        .forEach(li => tocList.appendChild(li));
}

function toggleToc() {
    if (getComputedStyle(leftMenu).opacity == 1) {
        leftMenu.style.opacity = 0;
        toggleTocButton.style.left = '15px';
    } else {
        leftMenu.style.opacity = 1;
        toggleTocButton.style.left = '315px';
    }
}
