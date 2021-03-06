package com.vodafone.frt.models;

import android.support.annotation.NonNull;

/**
 * Created by Ajay Tiwari on 2/23/2018.
 */

public class PTRResponseSelfCheckInModel {

 private int selfCheckInid;

 private String route_name;
 private String latitude;
 private String longitude;
 private String planned_start_time;
 private String planned_end_time;
 private String created_on;
 private String remarks;
 private String approved_on;
 private String manager_remarks;

 public int getSelfCheckInid() {
  return selfCheckInid;
 }

 public void setSelfCheckInid(int selfCheckInid) {
  this.selfCheckInid = selfCheckInid;
 }

 public String getRoute_name() {
  return route_name;
 }

 public void setRoute_name(String route_name) {
  this.route_name = route_name;
 }

 public String getLatitude() {
  return latitude;
 }

 public void setLatitude(String latitude) {
  this.latitude = latitude;
 }

 public String getLongitude() {
  return longitude;
 }

 public void setLongitude(String longitude) {
  this.longitude = longitude;
 }

 public String getPlanned_start_time() {
  return planned_start_time;
 }

 public void setPlanned_start_time(String planned_start_time) {
  this.planned_start_time = planned_start_time;
 }

 public String getPlanned_end_time() {
  return planned_end_time;
 }

 public void setPlanned_end_time(String planned_end_time) {
  this.planned_end_time = planned_end_time;
 }

 public String getCreated_on() {
  return created_on;
 }

 public void setCreated_on(String created_on) {
  this.created_on = created_on;
 }

 public String getRemarks() {
  return remarks;
 }

 public void setRemarks(String remarks) {
  this.remarks = remarks;
 }

 public String getApproved_on() {
  if(approved_on==null)
   return approved_on="";
  return approved_on;
 }

 public void setApproved_on(String approved_on) {
  this.approved_on = approved_on;
 }

 public String getManager_remarks() {
  if(manager_remarks==null)
   return manager_remarks="";
  return manager_remarks;
 }

 public void setManager_remarks(String manager_remarks) {
  this.manager_remarks = manager_remarks;
 }

 public String getStatus() {
  return status;
 }

 public void setStatus(String status) {
  this.status = status;
 }

 private String status;


}
