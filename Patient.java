package drpatients;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "patient")
public class Patient implements Comparable < Patient > {
 private String doctor; // person
 private String first; // his/her patient
 private String second;
 private String third;
 private int id; // identifier used as lookup-key

 public Patient() {}

 @Override
 public String toString() {
  return String.format("%2d: ", id) + doctor + "------\n" + first + "\n" + second + "\n" + third + "\n" + "\n";
 }


 //** properties
 public void setDoctor(String doctor) {
  this.doctor = doctor;
 }
 @XmlElement
 public String getDoctor() {
  return this.doctor;
 }

 public void setFirst(String first) {
  this.first = first;
 }
 @XmlElement
 public String getFirst() {
  return this.first;
 }


 public void setSecond(String second) {
  this.second = second;
 }
 @XmlElement
 public String getSecond() {
  return this.second;
 }

 public void setThird(String third) {
  this.third = third;
 }
 
 @XmlElement
 public String getThird() {
  return this.third;
 }


 public void setId(int id) {
  this.id = id;
 }
 @XmlElement
 public int getId() {
  return this.id;
 }

 // implementation of Comparable interface
 public int compareTo(Patient other) {
  return this.id - other.id;
 }
}
