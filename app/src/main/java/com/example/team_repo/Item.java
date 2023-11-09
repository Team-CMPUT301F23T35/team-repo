package com.example.team_repo;

import android.graphics.Bitmap;
import android.media.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Item implements Serializable {
    // TODO:
    //  - Add more constructors when certain arguments aren't given/are optional
    //  - Make less tedious constructors??? (less arguments?)
    //  - Put more restrictions on getters/setters (e.g. make sure inputted value isn't negative)
    //  - Come up with method to add a new tag to the tag list???
    //  - Potentially deal with empty description/comment/make/model/etc???

/**
 * Maintains information about a user's item.
 */


    private String name;
    private String purchase_date;
    private float value;
    private String description;
    private String make;
    private String model;
    private String serial_number;
    private String comment;
    private ArrayList<Tag> tags;
    private String imagePath; // Store the file path or URI of the image as a string


    /**
     * Constructor for an item when all information is provided.
     * @param name the item's name
     * @param purchase_date the item's purchase date
     * @param value the item's estimated value
     * @param description a description of the item
     * @param make the item's make
     * @param model the item's model
     * @param serial_number the item's serial number
     * @param tags an array list of the item's tags
     */
    public Item(String name, String purchase_date, float value, String description, String make, String model, String serial_number, String comment, ArrayList<Tag> tags, String imagePath) {
        this.name = name;
        this.purchase_date = purchase_date;
        this.value = value;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serial_number = serial_number;
        this.comment = comment;
        this.tags = tags;
        this.imagePath = imagePath;
    }

    /**
     * Constructor for an item when all information is provided, except for the item's tags and image.
     * @param name the item's name
     * @param purchase_date the item's purchase date
     * @param value the item's estimated value
     * @param description a description of the item
     * @param make the item's make
     * @param model the item's model
     * @param serial_number the item's serial number
     */
    public Item(String name, String purchase_date, float value, String description, String make, String model, String serial_number, String comment) {
        this.name = name;
        this.purchase_date = purchase_date;
        this.value = value;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serial_number = serial_number;
        this.comment = comment;
        this.tags = null;
        this.imagePath = null;
    }

    /**
     * Return the name of the item.
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the name of the item to the given name.
     * @param name the new name of the item
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the purchase date of the item.
     * @return the purchase date of the item.
     */
    public String getDate() {
        return purchase_date;
    }

    /**
     * Changes the purchase date of the item to the given purchase date.
     * @param purchase_date the new purchase date of the item
     */
    public void setDate(String purchase_date) {
        this.purchase_date = purchase_date;
    }

    /**
     * Returns the estimated value of the item.
     * @return the estimated value of the item
     */
    public float getValue() {
        return value;
    }

    /**
     * Changes the estimated value of the item to the given value.
     * @param value the new estimated value of the item
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Returns the description of the item.
     * @return the description of the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Changes the description of the item to the given description.
     * @param description the new description of the item.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the make of the item.
     * @return the make of the item
     */
    public String getMake() {
        return make;
    }

    /**
     * Changes the make of the item to the given make.
     * @param make the new make of the item
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Returns the model of the item.
     * @return the model of the item
     */
    public String getModel() {
        return model;
    }

    /**
     * Changes the model of the item to the given model.
     * @param model the new model of the item
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the serial number of the item.
     * @return the serial number of the item
     */
    public String getSerialNumber() {
        return serial_number;
    }

    /**
     * Changes the serial number of the item to the given serial number.
     * @param serial_number the new serial number of the item
     */
    public void setSerialNumber(String serial_number) {
        this.serial_number = serial_number;
    }

    /**
     * Returns the comment on the item.
     * @return the comment on the item
     */
    public String getComment() {
        return comment;
    }

    /**
     * Changes the comment on the item to the given comment.
     * @param comment the new comment on the item
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Returns an array list of the item's tags.
     * @return an array list of the item's tags
     */
    public ArrayList<Tag> getTags() {
        return tags;
    }

    /**
     * Changes the tags of the item to the given array list of tags.
     * @param tags the new tags of the item
     */
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Returns the image of the item.
     * @return the image of the item
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Changes the image of the item to the given image.
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setAllNull(){
        this.name = null;
        this.purchase_date = null;
//        String valueString = String.valueOf(this.value);
//        valueString = null;
        this.value = 0;
        this.description = null;
        this.make = null;
        this.model = null;
        this.serial_number = null;
        this.comment = null;
//        this.tags = tags;
//        this.image = image;
    }

    public boolean checkAllNull(){
        if (this.name == null && this.purchase_date == null && this.value == 0 && this.description == null && this.make == null && this.model == null && this.serial_number == null && this.comment == null){
            return true;
        }
        return false;
    }

}
