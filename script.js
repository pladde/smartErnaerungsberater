let dataName = document.getElementsByName("file");

// Prüft ob es sich um ein erlaubes Datenformat handelt
function validateDragAndDrop(dataName) {

    let allowedFormats = ['csv', 'json', 'xml'];

    for(let i = 0; i <= allowedFormats.length; i++) {
        if(dataName.contains(allowedFormats[i]) === false) {
            alert("please choose an allowed data format!");
        }
    }
}