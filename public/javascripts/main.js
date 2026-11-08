let hocon = window.document.querySelector('#hocon');
let output = window.document.querySelector('#output');

let inputResolve = window.document.querySelector('#resolve');
let inputJson = window.document.querySelector('#json');
let inputFormatted = window.document.querySelector('#formatted');
let inputComments = window.document.querySelector('#comments');
let inputOriginComments = window.document.querySelector('#originComments');


function debounce(func, delay) {
    let timeoutId;
    return function (...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func.apply(this, args), delay);
    };
}

const debouncedUpdateOutput = debounce(updateOutput, 300);

inputResolve.addEventListener('input', debouncedUpdateOutput)
inputJson.addEventListener('input', debouncedUpdateOutput)
inputFormatted.addEventListener('input', debouncedUpdateOutput)
inputComments.addEventListener('input', debouncedUpdateOutput)
inputOriginComments.addEventListener('input', debouncedUpdateOutput)
hocon.addEventListener('keyup', debouncedUpdateOutput);

function updateOutput() {
    const query =
        `resolve=${inputResolve.checked}` +
        `&json=${inputJson.checked}` +
        `&formatted=${inputFormatted.checked}` +
        `&comments=${inputComments.checked}` +
        `&originComments=${inputOriginComments.checked}`;
    let r = fetch(`/hoconToJson?${query}`, {
        method: 'POST',
        body: hocon.innerText,
    });
    r.then(x => x.text()).then(renderedJson => {
        output.innerText = renderedJson;
    });
}

updateOutput();
