package fr.upem.ijavabook.exmanager;

import java.util.Objects;
import java.util.Observable;

/**
 * Mutable state of a markdown translation.
 * Created by steeve on 05/12/15.
 */
class HtmlObservable extends Observable{
    private String htmlTranslation;

    HtmlObservable(String str){
        super();
        htmlTranslation = str;
    }

    void setHtmlTranslation(String str){
        Objects.requireNonNull(str);
        htmlTranslation = str;
        setChanged();
        notifyObservers(htmlTranslation);
    }

    String getHtml(){
        return htmlTranslation;
    }

}
