package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringWriter;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public class Converter {
@SuppressWarnings("unchecked")
public static String csvToJson(String csvString) {
    // Create a JSON object to hold the result
    JsonObject resultJson = new JsonObject();

    // Create JSON arrays to store product numbers, data, and column headings
    JsonArray prodNums = new JsonArray();
    JsonArray data = new JsonArray();
    List<String> headings = new ArrayList<>();

    try {
        // Create a StringReader to read the CSV string
        StringReader stringReader = new StringReader(csvString);
        
        // Create a CSVReader to parse the CSV data, skipping the first line (headers)
        CSVReader csvReader = new CSVReaderBuilder(stringReader).withSkipLines(0).build();
        
        // Read all rows from the CSV file
        List<String[]> rows = csvReader.readAll();

        if (!rows.isEmpty()) {
            // Get the headers (first row) from the CSV file
            String[] headers = rows.get(0);

            for (String header : headers) {
                // Add each header to the list of column headings
                headings.add(header);
            }

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                prodNums.add(row[0]); // Add the product number to the product numbers array

                JsonArray dataRow = new JsonArray();
                for (int j = 1; j < row.length; j++) {
                    try {
                        // Try to parse the value as an integer and add it to the data row
                        int intValue = Integer.parseInt(row[j]);
                        dataRow.add(intValue);
                    } catch (NumberFormatException e) {
                        // If parsing as an integer fails, add the value as a string
                        dataRow.add(row[j]);
                    }
                }
                // Add the data row to the data array
                data.add(dataRow);
            }

            // Add the JSON arrays to the result JSON object
            resultJson.put("ProdNums", prodNums);
            resultJson.put("ColHeadings", headings);
            resultJson.put("Data", data);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Convert the result JSON object to a JSON string and return it
    return resultJson.toString();
}

@SuppressWarnings("unchecked")
public static String jsonToCsv(String jsonString) {
    // Initialize the CSV result as an empty string
    String csvResult = "";

    try {
        // Create a StringWriter to write CSV data
        StringWriter writer = new StringWriter();
        // Create a CSVWriter with specific settings
        CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");

        // Parse the JSON string into a JsonObject
        JsonObject jsonData = Jsoner.deserialize(jsonString, new JsonObject());

        // Get the column headings from the JSON data
        JsonArray columnHeadings = (JsonArray) jsonData.get("ColHeadings");
        String[] headings = columnHeadings.toArray(String[]::new);

        // Write the column headings to the CSV writer
        csvWriter.writeNext(headings);

        // Get the product numbers and data rows from the JSON data
        JsonArray productNumbers = (JsonArray) jsonData.get("ProdNums");
        JsonArray dataRows = (JsonArray) jsonData.get("Data");

        for (int i = 0; i < dataRows.size(); i++) {
            // Get a row of data from the data rows array
            JsonArray row = (JsonArray) dataRows.get(i);
            String[] rowData = new String[row.size() + 1];

            // Add the product number as the first element of the rowData array
            rowData[0] = productNumbers.getString(i);

            for (int j = 0; j < row.size(); j++) {
                // Get a value from the data row
                String value = row.getString(j);

                if (j + 1 == 3) {
                    // If it's the third column (index 2), format it as two digits
                    rowData[j + 1] = String.format("%02d", Integer.valueOf(value));
                } else {
                    // Otherwise, add the value as is
                    rowData[j + 1] = value;
                }
            }

            // Write the rowData array to the CSV writer
            csvWriter.writeNext(rowData);
        }

        // Convert the CSV data in the StringWriter to a string
        csvResult = writer.toString();

    } catch (Exception e) {
        e.printStackTrace();
    }

    // Trim any leading/trailing whitespace and return the CSV result
    return csvResult.trim();
}
}
