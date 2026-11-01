package hospital.model;

public class Medicine {
    private String id;
    private String name;
    private String category;
    private int stockQty;
    private double unitPrice;
    private String expiryDate;
    private String manufacturer;

    public Medicine() {
    }

    public Medicine(String id, String name, String category, int stockQty, double unitPrice, String expiryDate, String manufacturer) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.stockQty = stockQty;
        this.unitPrice = unitPrice;
        this.expiryDate = expiryDate;
        this.manufacturer = manufacturer;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getStockQty() { return stockQty; }
    public void setStockQty(int stockQty) { this.stockQty = stockQty; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
}

