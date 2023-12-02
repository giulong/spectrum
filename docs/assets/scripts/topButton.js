const topButton = document.getElementById("topButton");
const topButtonOffset = 350;

window.onscroll = function() { scrollFunction() };

function scrollFunction() {
    if (document.body.scrollTop > topButtonOffset || document.documentElement.scrollTop > topButtonOffset) {
        topButton.style.display = 'block';
    } else {
        topButton.style.display = 'none';
    }
}

function topFunction() {
    document.body.scrollTop = 0; // For Safari
    document.documentElement.scrollTop = 0; // For Chrome, Firefox, IE and Opera
}
