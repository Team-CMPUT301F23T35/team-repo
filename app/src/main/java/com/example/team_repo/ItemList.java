package com.example.team_repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Maintains an item list and the total estimated value for all items in the list.
 */
public class ItemList {

    private ArrayList<Item> item_list;
    private float total_value;

    /**
     * Constructor for a new empty item list.
     */
    public ItemList() {
        this.item_list = new ArrayList<Item>();
        this.total_value = 0;
    }

    /**
     * Adds an item to the item list and updates the total estimated value of the list.
     * @param item the item to be added to the list
     */
    public void add(Item item) {
        item_list.add(item);
        this.total_value = this.total_value + item.getValue();
    }

    public void updateValue() {
        float sumItem = 0;
        for (Item item : this.item_list) {
            // Your code to handle each item goes here
            // For example, you can access properties of the 'item' object
            sumItem = sumItem + item.getValue();
            // ... other properties
        };
        this.total_value = sumItem;

    }

    public void removeNullItem(){
        for (Item item : this.item_list) {
            if (item.checkAllNull()){
                remove(item);
            }
        }
    }
    /**
     * Removes an item from the item list and updates the total estimated value of the list.
     * @param item the item to be removed from the list
     */
    public void remove(Item item) {
        item_list.remove(item);
        this.total_value = this.total_value - item.getValue();
    }

    /**
     * Returns the item list.
     * @return the item list
     */
    public ArrayList<Item> getList() {
        return item_list;
    }

    /**
     * Returns the total estimated value of the item list.
     * @return the total estimated value of the item list
     */
    public float getTotalValue() {
        return this.total_value;
    }

//    public void sort(ItemList item_list_sort) {
//        Collections.sort(item_list_sort.getList(), nameComparator);
//
//    }
//    Comparator<Item> nameComparator = new Comparator<Item>() {
//        @Override
//        public int compare(Item item1, Item item2) {
//            return item1.getName().compareTo(item2.getName());
//        }
//    };
}

