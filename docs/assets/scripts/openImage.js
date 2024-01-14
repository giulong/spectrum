const images = Array.from(document.querySelectorAll('img'));

images
    .filter(image => !image.classList.contains('copy-icon') && image.id != 'spectrum-logo')
    .forEach(image => image.addEventListener('click', () => window.open(image.getAttribute('src'))));
