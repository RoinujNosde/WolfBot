package me.roinujnosde.wolfbot.models.spiget;

import java.util.List;
import java.util.Locale;

public class Resource {

    private final long id;
    private final String name;
    private final String tag;
    private final List<String> testedVersions;
    private final Rating rating;
    private final Icon icon;
    private final File file;
    private final int downloads;
    private final double price;
    private final String currency;
    private final boolean premium;

    public Resource(long id,
                    String name,
                    String tag,
                    List<String> testedVersions,
                    Rating rating,
                    Icon icon,
                    File file,
                    int downloads,
                    double price,
                    String currency, boolean premium) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.testedVersions = testedVersions;
        this.rating = rating;
        this.icon = icon;
        this.file = file;
        this.downloads = downloads;
        this.currency = currency;
        this.price = price;
        this.premium = premium;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Rating getRating() {
        return rating;
    }

    public Icon getIcon() {
        return icon;
    }

    public long getId() {
        return id;
    }

    public List<String> getTestedVersions() {
        return testedVersions;
    }

    public int getDownloads() {
        return downloads;
    }

    public String getPrice() {
        if (currency != null) {
            return String.format(Locale.ENGLISH, "%.2f %S", price, currency);
        }
        return "Free";
    }

    public File getFile() {
        return file;
    }

    public boolean isPremium() {
        return premium;
    }
}
