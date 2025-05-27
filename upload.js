let API_BASE_URL = "http://localhost:8080/api";

export async function sendFileToServer(file) {
    let formData = new FormData();
    formData.append('file', file);
}

export async function postData(file){
    try {
        let response = await fetch(`${API_BASE_URL}/products`, {
            method: 'POST',
            body: file,
        });
    }
    catch (error) {
        console.log("Fetch on http://localhost:8080/api/products failed!")

    }
}