const border = arguments[0].style.border;
const borderRadius = arguments[0].style.borderRadius;

arguments[0].style.border = '3px solid red';
arguments[0].style.borderRadius = '5px';

setTimeout(() => {
    arguments[0].style.border = border;
    arguments[0].style.borderRadius = borderRadius;
}, 500);
