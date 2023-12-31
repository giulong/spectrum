const leftMenu = document.getElementById('left-menu');
const headings = Array.from(document.querySelectorAll('h1, h2, h3, h4'));
const tocList = document.getElementById('toc-list');
const toggleTocButton = document.getElementById('toggle-toc-button');

document.onload = buildLeftMenu();

function buildLeftMenu() {
    headings
        .map(heading => {
            const li = document.createElement('li');
            const text = heading.innerText;

            li.setAttribute('onclick', 'navigateTo("' + text.toLowerCase().replaceAll(' ', '-') + '")');
            li.classList.add('toc-element', heading.nodeName.toLowerCase());
            li.innerText = text;

            return li;
        })
        .forEach(li => tocList.appendChild(li));
}

function toggleToc() {
    if (getComputedStyle(leftMenu).opacity == 1) {
        leftMenu.style.opacity = 0;
        leftMenu.style.visibility = 'hidden';
        toggleTocButton.style.left = '15px';
    } else {
        leftMenu.style.opacity = 1;
        leftMenu.style.visibility = 'visible';
        toggleTocButton.style.left = '315px';
    }
}
