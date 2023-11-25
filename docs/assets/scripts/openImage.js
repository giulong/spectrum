const images = Array.from(document.querySelectorAll('img'));

images
    .filter(image => !image.classList.contains('copy-icon'))
    .forEach(image => image.addEventListener('click', () => window.open(image.getAttribute('src'))));
