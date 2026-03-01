const url = `http://localhost:${arguments[0]}/`;
const options = { capture: true, passive: true };
const allNodes = document.getElementsByTagName('*');

function post(type, data) {
    var action = buildActionFor(type, data);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, false);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.send(action);
}

function buildActionFor(type, data) {
    return JSON.stringify({
        "type": type,
        "data": data
    });
}

navigation.addEventListener('navigate', e => {
    if ("traverse" === e.navigationType) {
        post('traverse', e.destination.url);
    } else {
        post('navigate', e.destination.url);
    }
}, options);

addEventListener('change', e => {
    if (e.target.type === 'text') {
        post('input', JSON.stringify({ "path": buildXPathOf(e.target), "value": e.target.value }));
    }
}, options);

addEventListener('click', e => post('click', buildXPathOf(e.target)), options);

function buildXPathOf(element) {
    for (segments = []; element && element.nodeType == 1; element = element.parentNode) {
        if (element.hasAttribute('id')) {
            var idCount = 0;

            for (i = 0; i < allNodes.length; i++) {
                if (allNodes[i].hasAttribute('id') && allNodes[i].id == element.id) {
                    if (++idCount > 1) {
                        break;
                    }
                }
            }

            var id = element.getAttribute('id');
            if (idCount == 1) {
                segments.unshift('id("' + id + '")');
                return segments.join('/');
            }
            
            segments.unshift(element.localName.toLowerCase() + '[@id="' + id + '"]');
        } else if (element.hasAttribute('class')) {
            segments.unshift(element.localName.toLowerCase() + '[@class="' + element.getAttribute('class') + '"]');
        } else {
            for (i = 1, sibling = element.previousSibling; sibling; sibling = sibling.previousSibling) {
                if (sibling.localName == element.localName) {
                    i++;
                }
            }

            segments.unshift(element.localName.toLowerCase() + '[' + i + ']');
        }
    }

    return segments.length ? '/' + segments.join('/') : null;
};
