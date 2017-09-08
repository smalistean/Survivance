//*************************************************//
//          INTHER LOGISTICS ENGINEERING           //
//*************************************************//
package alisgroup.titanicmanipulator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author agore
 */
public class CSV {

  public static List<Person> readPersons(String path) throws FileNotFoundException, IOException {
    List<Person> persons = new ArrayList<>();
    CSVParser parser = new CSVParser(new FileReader(path), CSVFormat.DEFAULT.withHeader());
    for (CSVRecord record : parser) {
      Person person = new Person();
      person.setId(Integer.parseInt(record.get("PassengerId")));
      person.setSurvived(Integer.parseInt(record.get("Survived")));
      person.setpClass(Integer.parseInt(record.get("Pclass")));
      person.setName(record.get("Name"));
      person.setSex(record.get("Sex").equals("male") ? 0 : 1);
//      person.setAge(record.get("Age").equals("") ? null : (long) Float.parseFloat(record.get("Age")));
      person.setAge(record.get("Age"));
      person.setSibSp(Integer.parseInt(record.get("SibSp")));
      person.setParCh(Integer.parseInt(record.get("Parch")));
      person.setTicket(record.get("Ticket"));
      person.setFare(record.get("Fare").equals("") ? 0 : (long) Float.parseFloat(record.get("Fare")));
      switch (record.get("Embarked")) {
        case "C":
          person.setEmbarked(1);
        case "Q":
          person.setEmbarked(2);
        case "S":
          person.setEmbarked(3);
      }
      persons.add(person);
    }
    return persons;
  }

  public void exportCSV(List<Person> persons, String path) {
    try {
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
      for (Person person : persons) {
        StringBuffer oneLine = new StringBuffer();
        oneLine.append(person.getId());
        oneLine.append(",");
        oneLine.append(person.getpClass());
        oneLine.append(",");
        oneLine.append(person.getTitle());
        oneLine.append(",");
        oneLine.append(person.getName());
        oneLine.append(",");
        oneLine.append(person.getSibSp() + person.getParCh());
        oneLine.append(",");
        oneLine.append(person.getAge());
        oneLine.append(",");
        oneLine.append(person.getSex());
        oneLine.append(",");
        oneLine.append(person.getFare());
        oneLine.append(",");
        oneLine.append(person.getTicket());
        oneLine.append(",");
        oneLine.append(person.getEmbarked());
        oneLine.append(",");
        oneLine.append(person.getSurvived());
        bw.write(oneLine.toString());
        bw.newLine();
        bw.flush();
      }
      bw.close();
    } catch (UnsupportedEncodingException e) {
      System.out.println("UnsupportedEncodingException");
    } catch (FileNotFoundException e) {
      System.out.println("FileNotFoundException");
    } catch (IOException e) {
      System.out.println("IOException");
    }
  }
}