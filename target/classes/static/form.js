function submitForm() {
    const form = document.getElementById('myForm');
    const formData = {};
    const inputs = form.getElementsByTagName('input');
    for (let i = 0; i < inputs.length; i++) {
        const input = inputs[i];
        if (input.name !== 'stringData') {
            // Exclude 'stringData' key
            formData[input.name] = input.value;
        }
    }
    const textArea = form.getElementsByTagName('textarea')[0];
    formData[textArea.name] = textArea.value;
    const stringData = JSON.stringify(formData);
    document.getElementById('stringData').value = stringData;
    // Call the endpoint with the form data\n"
    const endpoint = '/documents/" + databaseName + "/" + collectionName + "';
    fetch(endpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: stringData,
    })
        .then((response) => {
            if (response.status === 201) {
                console.log('Data sent successfully');
            } else {
                console.error('Failed to send data');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}
