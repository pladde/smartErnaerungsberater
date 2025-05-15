let dataContent;

function saveData(files) {
    dataContent = files;
    validateDataFormat();
}

// Prüft ob es sich um ein erlaubes Datenformat handelt
function validateDataFormat() {

    let foundDataName = dataContent[0].name;
    console.log("found file: " + foundDataName);
    let formatIsAllowed = false;

    let validFormats = ['.json', '.xml','.csv'];
    console.log("valid formats: " + validFormats);

    console.log("array length: " + validFormats.length);

    for(let i = 0; i < validFormats.length; i++) {

        console.log("while go through: " + i);
        if(foundDataName.includes(validFormats[i])) {
            console.log("found valid format: " + validFormats[i])
            formatIsAllowed = true;
        }
    }

    if(formatIsAllowed === false) {
        console.log("no valid format found");
        alert("Please choose a valid data-format!")
    }

    return formatIsAllowed;
}

document.addEventListener("DOMContentLoaded", function () {
    let sendButton = document.getElementById("send");

    sendButton.addEventListener("click", function () {
        // Code zum übermitteln hier

        console.log("CLICK/found data name: " + dataContent.name);
        if(validateDataFormat(dataContent.name)) {
            alert("Click successfully!");
        }
        else {
            alert("Please insert a valid data-format and try again!");
        }
    });
});
