package com.example.NoSQLDecentralizedDatabase.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
public class FormGeneratorService {
    @ResponseBody
    public static String generateFormFromSchema(String schemaJson, String databaseName) {
        try {
            JSONObject schema = new JSONObject(schemaJson);
            String collectionName = schema.getString("collectionName");
            JSONArray properties = schema.getJSONArray("properties");

            StringBuilder formHtml = new StringBuilder();

            // Start the form element
            formHtml.append("<form id=\"myForm\">");

            // Create a hidden input field for the string data
            formHtml.append("<input type=\"hidden\" name=\"stringData\" id=\"stringData\" value=\"\">\n");

            formHtml.append("<h2>").append(collectionName).append(" Form</h2>");
            formHtml.append("<table>");
            for (int i = 0; i < properties.length(); i++) {
                JSONObject property = properties.getJSONObject(i);
                String propertyName = property.getString("name");
                String propertyType = property.getString("type");

                formHtml.append("<tr>");
                formHtml.append("<td><label for=\"").append(propertyName).append("\">").append(propertyName).append(":</label></td>");
                formHtml.append("<td>");

                switch (propertyType) {
                    case "string" ->
                            formHtml.append("<input type=\"text\" id=\"").append(propertyName).append("\" name=\"").append(propertyName).append("\" required><br>");
                    case "integer" ->
                            formHtml.append("<input type=\"number\" id=\"").append(propertyName).append("\" name=\"").append(propertyName).append("\" required><br>");
                    case "array" ->
                            formHtml.append("<textarea id=\"").append(propertyName).append("\" name=\"").append(propertyName).append("\" rows=\"4\" cols=\"50\"></textarea><br>");
                }
                formHtml.append("</td></tr>");
            }
            formHtml.append("</table>");

            // Add a button to trigger the form submission
            formHtml.append("<button type=\"button\" onclick=\"submitForm()\">Submit</button>");

            // End the form element
            formHtml.append("</form>");

            // JavaScript code for handling form submission

            String script = "<script>\n" +
                    "function submitForm() {\n" +
                    "    const form = document.getElementById('myForm');\n" +
                    "    const formData = {};\n" +
                    "    const inputs = form.getElementsByTagName('input');\n" +
                    "    for (let i = 0; i < inputs.length; i++) {\n" +
                    "        const input = inputs[i];\n" +
                    "        if (input.name !== 'stringData') {\n" + // Exclude 'stringData' key
                    "            formData[input.name] = input.value;\n" +
                    "        }\n" +
                    "    }\n" +
                    "    const textArea = form.getElementsByTagName('textarea')[0];\n" +
                    "    formData[textArea.name] = textArea.value;\n" +
                    "    const stringData = JSON.stringify(formData);\n" +
                    "    document.getElementById('stringData').value = stringData;\n" +
                    "    // Call the endpoint with the form data\n" +
                    "    const endpoint = '/documents/" + databaseName + "/" + collectionName + "';\n" +
                    "    fetch(endpoint, {\n" +
                    "        method: 'POST',\n" +
                    "        headers: {\n" +
                    "            'Content-Type': 'application/json'\n" +
                    "        },\n" +
                    "        body: stringData\n" +
                    "    })\n" +
                    "    .then(response => {\n" +
                    "        if (response.status === 201) {\n" +
                    "            console.log('Data sent successfully');\n" +
                    "        } else {\n" +
                    "            console.error('Failed to send data');\n" +
                    "        }\n" +
                    "    })\n" +
                    "    .catch(error => {\n" +
                    "        console.error('Error:', error);\n" +
                    "    });\n" +
                    "}\n" +
                    "</script>\n";

            formHtml.append(script);

            return formHtml.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error: Invalid schema format";
        }
    }

}


