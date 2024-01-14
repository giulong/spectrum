const leftMenu = document.getElementById('left-menu');
const originalLeftMenuWidth = getComputedStyle(leftMenu).width;
const headings = Array.from(document.querySelectorAll('h1, h2, h3, h4'));
const tocList = document.getElementById('toc-list');
const toggleTocButton = document.getElementById('toggle-toc-button');
const originalToggleTocButtonWidth = getComputedStyle(toggleTocButton).width;

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
    if (getComputedStyle(leftMenu).width == '0px') {
        leftMenu.style.width = originalLeftMenuWidth;
        leftMenu.style.opacity = 1;
        toggleTocButton.style.width = originalToggleTocButtonWidth;
        setTimeout(() => toggleTocButton.innerText = 'Toggle Toc', .05);
    } else {
        leftMenu.style.width = '0px';
        leftMenu.style.opacity = 0;
        toggleTocButton.style.width = '65px';
        setTimeout(() => toggleTocButton.innerText = 'Toggle', .05);
    }
}
