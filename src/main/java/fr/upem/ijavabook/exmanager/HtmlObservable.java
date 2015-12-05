package fr.upem.ijavabook.exmanager;

import java.util.Objects;
import java.util.Observable;

/**
 * Mutable state of a markdown traduction.
 * Created by steeve on 05/12/15.
 */
class HtmlObservable extends Observable{
    private String htmlTraduction;

    HtmlObservable(String str){
        super();
        htmlTraduction = str;
    }

    void setHtmlTraduction(String str){
        Objects.requireNonNull(str);
        htmlTraduction = str;
        notifyObservers(str);
    }

    String getHtml(){
        return htmlTraduction;
    }

}
