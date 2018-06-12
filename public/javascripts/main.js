let hocon = window.document.querySelector('#hocon');
let output = window.document.querySelector('#output');

let inputResolve = window.document.querySelector('#resolve');
let inputJson = window.document.querySelector('#json');
let inputFormatted = window.document.querySelector('#formatted');
let inputComments = window.document.querySelector('#comments');
let inputOriginComments = window.document.querySelector('#originComments');


inputResolve.addEventListener('input', updateOutput)
inputJson.addEventListener('input', updateOutput)
inputFormatted.addEventListener('input', updateOutput)
inputComments.addEventListener('input', updateOutput)
inputOriginComments.addEventListener('input', updateOutput)
hocon.addEventListener('keyup', updateOutput);

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
