package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
        // Create Outer JSON object.
        JsonObject json = new JsonObject();
        
        // Create "Inner" JSON Objects.
        JsonObject scheduletype = new JsonObject();
        JsonObject subject = new JsonObject();
        JsonObject course = new JsonObject();
        JsonArray sectionArray = new JsonArray();
        
        // Set up CSV Iterator; Get Headerr Row
        Iterator<String[]> iterator = csv.iterator();
        
        String[] headers = new String[0];

        if (iterator.hasNext()) {
            headers = iterator.next();
        }
        
        HashMap<String, Integer> headerMap = new HashMap<>();
        
        for (int i = 0; i < headers.length; ++i) {
            headerMap.put(headers[i], i);
        }
        

        
        // Process CSV Records
        while (iterator.hasNext()){
            
            
            
            String[] record = iterator.next();
            
            int crnColumn = Integer.valueOf(record[ headerMap.get(CRN_COL_HEADER) ]);

            
            // Put record 2 into a list
            List<String> sectionValues = new ArrayList<>();
            for (String value : record[ headerMap.get(NUM_COL_HEADER) ].split("\\s+")) {
                sectionValues.add(value);
            }
            
            // Extract relevant information from the CSV row
            // Save values for Course
            String subjectId = sectionValues.get(0);
            String courseNumber = sectionValues.get(1);
            String description = record[ headerMap.get(DESCRIPTION_COL_HEADER) ];
            int credits = Integer.parseInt(record[ headerMap.get(CREDITS_COL_HEADER) ]);
            
            // Save instructors to a list.
            List<String> InstructorValues = new ArrayList<>();
            for (String value : record[ headerMap.get(INSTRUCTOR_COL_HEADER) ].split(",")) {
                InstructorValues.add(value.trim());
            }
            
            // Save values for section.
            int crn = Integer.parseInt(record[ headerMap.get(CRN_COL_HEADER) ]);
            String Section = record[ headerMap.get(SECTION_COL_HEADER) ];
            String type = record[ headerMap.get(TYPE_COL_HEADER) ];
            String start = record[ headerMap.get(START_COL_HEADER) ];
            String end = record[ headerMap.get(END_COL_HEADER) ];
            String days = record[ headerMap.get(DAYS_COL_HEADER) ];
            String where = record[ headerMap.get(WHERE_COL_HEADER) ];
            
            // Create a JSON object for each course
            JsonObject course1 = new JsonObject();
            course1.put("subjectid", subjectId);
            course1.put("num", courseNumber);
            course1.put("description", description);
            course1.put("credits", credits);
            course.put(record[ headerMap.get(NUM_COL_HEADER) ],course1);
            
            // Create Json Objects for section
            JsonObject section = new JsonObject();
            section.put(CRN_COL_HEADER,crn);
            section.put(SECTION_COL_HEADER,Section);
            section.put(TYPE_COL_HEADER,type);
            section.put(START_COL_HEADER,start);
            section.put(END_COL_HEADER,end);
            section.put(DAYS_COL_HEADER,days);
            section.put(WHERE_COL_HEADER,where);
            section.put(INSTRUCTOR_COL_HEADER, InstructorValues);
            section.put(SUBJECTID_COL_HEADER, subjectId);
            section.put(NUM_COL_HEADER,courseNumber);
            sectionArray.add(section);

            
            
            
            
            // Create JsonObjects for schedule.
            scheduletype.put(record[ headerMap.get(TYPE_COL_HEADER) ],record[ headerMap.get(SCHEDULE_COL_HEADER) ]);
            
            // Create JsonObject for Subject.
            subject.put(sectionValues.get(0),record[ headerMap.get(SUBJECT_COL_HEADER) ]);
            

            
        }
        
        // Add the JSON object to the result JSON
        json.put("scheduletype",scheduletype );
        json.put("subject", subject);
        json.put("course",course );
        json.put("section",sectionArray);
            
        // Convert the final JSON object to a string
        //String jsonString = json.toJson();
        ///System.out.println(jsonString);
        
        return Jsoner.serialize(json);
        
    }
        
    public String convertJsonToCsvString(JsonObject json) {
// Create csv string
        String csvString;
        
        // Create stringwriter
        StringWriter sWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(sWriter, '\t', '"', '\\', "\n");
        
        // create headers and write to csv
        String[] headers = {CRN_COL_HEADER,
            SUBJECT_COL_HEADER
                , NUM_COL_HEADER
                , DESCRIPTION_COL_HEADER
                , SECTION_COL_HEADER
                , TYPE_COL_HEADER
                ,CREDITS_COL_HEADER
                , START_COL_HEADER
                , END_COL_HEADER
                , DAYS_COL_HEADER
                , WHERE_COL_HEADER
                , SCHEDULE_COL_HEADER
                , INSTRUCTOR_COL_HEADER
        };
        csvWriter.writeNext(headers);
        
        // Create json object and array for section
        JsonObject scheduletype = (JsonObject) json.get("scheduletype");
        
        JsonObject subjects = (JsonObject) json.get("subject");
        
        JsonObject courses = (JsonObject) json.get("course");
        
        JsonArray sections = (JsonArray) json.get("section");
        
        // Initialize all variables.
        String crn
                , subject
                , num
                , description
                , subjectID
                , type
                , credits
                , start
                , end
                , days
                , where
                , schedule
                , instructor
                ;
        
        // create advanced for loop to iterate through
        for (Object sectObj : sections) {
            JsonObject sectionss = (JsonObject) sectObj;
            
            // Collect first three values (crn, subject, and num) for current CSV row
            crn = String.valueOf(sectionss.get(CRN_COL_HEADER));
            subject = (String) subjects.get((String) sectionss.get(SUBJECTID_COL_HEADER));
            num = ((String) sectionss.get(SUBJECTID_COL_HEADER)) + " " + ((String) sectionss.get(NUM_COL_HEADER));
            
            // Create a new json object for all json objects in courses
            JsonObject cCourse = (JsonObject) courses.get(num); // references most recent number grabbed to pull specific course
            
            // use get to retrieve values from jsonobjects.
            description = (String) cCourse.get(DESCRIPTION_COL_HEADER);
            subjectID = (String) sectionss.get(SECTION_COL_HEADER);
            type = (String) sectionss.get(TYPE_COL_HEADER);
            credits = String.valueOf(cCourse.get(CREDITS_COL_HEADER));
            start = (String) sectionss.get(START_COL_HEADER);
            end = (String) sectionss.get(END_COL_HEADER);
            days = (String) sectionss.get(DAYS_COL_HEADER);
            where = (String) sectionss.get(WHERE_COL_HEADER);
            schedule = (String) scheduletype.get(type);
            
            // Populate a List with instructors
            List<String> instructors = (List<String>) sectionss.get(INSTRUCTOR_COL_HEADER);
            instructor = String.join(", ", instructors);
            
            // Write current CSV row
            csvWriter.writeNext(new String[]{crn
                    , subject
                    , num
                    , description
                    , subjectID
                    , type
                    , credits
                    , start
                    , end
                    , days
                    , where
                    , schedule
                    , instructor});
            
            
        }
        
        // Write to csvString to return CSV
        csvString = sWriter.toString();
        
        return csvString; 
        
    }
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}