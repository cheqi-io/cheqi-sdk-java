package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Unit of measure codes based on UN/ECE Recommendation 20.
 *
 * <p>These codes are used to specify the unit of measure for product quantities
 * in receipts and invoices. They are required for compliance with e-invoicing
 * standards like Peppol and EN16931.</p>
 *
 * <p>Common examples:</p>
 * <ul>
 *   <li><strong>PIECE</strong> - Individual items (most common for retail)</li>
 *   <li><strong>KILOGRAM</strong> - Weight-based products (groceries, bulk items)</li>
 *   <li><strong>LITER</strong> - Volume-based products (liquids)</li>
 *   <li><strong>HOUR</strong> - Time-based services (consulting, labor)</li>
 * </ul>
 *
 * @see <a href="https://unece.org/trade/uncefact/cl-recommendations">UN/ECE Recommendation 20</a>
 */
public enum UnitCode {

    // ===== MOST COMMON UNITS =====

    /**
     * One (piece, unit).
     * Most common unit for retail products - individual items.
     * Peppol/UN/ECE Code: C62
     */
    ONE("C62", "One"),

    /**
     * Each.
     * Alternative to ONE, commonly used in North America.
     * Peppol/UN/ECE Code: EA
     */
    EACH("EA", "Each"),

    // ===== WEIGHT UNITS =====

    /**
     * Kilogram.
     * Used for weight-based products (groceries, bulk items).
     * Peppol/UN/ECE Code: KGM
     */
    KILOGRAM("KGM", "Kilogram"),

    /**
     * Gram.
     * Used for small weight measurements.
     * Peppol/UN/ECE Code: GRM
     */
    GRAM("GRM", "Gram"),

    /**
     * Milligram.
     * Used for very small weight measurements.
     * Peppol/UN/ECE Code: MGM
     */
    MILLIGRAM("MGM", "Milligram"),

    /**
     * Tonne (metric ton).
     * 1000 kilograms.
     * Peppol/UN/ECE Code: TNE
     */
    TONNE("TNE", "Tonne"),

    /**
     * Pound.
     * Used in imperial/US systems.
     * Peppol/UN/ECE Code: LBR
     */
    POUND("LBR", "Pound"),

    /**
     * Ounce.
     * Used in imperial/US systems.
     * Peppol/UN/ECE Code: ONZ
     */
    OUNCE("ONZ", "Ounce"),

    // ===== VOLUME UNITS =====

    /**
     * Liter.
     * Used for volume-based liquids.
     * Peppol/UN/ECE Code: LTR
     */
    LITER("LTR", "Liter"),

    /**
     * Milliliter.
     * Used for small volume measurements.
     * Peppol/UN/ECE Code: MLT
     */
    MILLILITER("MLT", "Milliliter"),

    /**
     * Centiliter.
     * Used for beverage measurements.
     * Peppol/UN/ECE Code: CLT
     */
    CENTILITER("CLT", "Centiliter"),

    /**
     * Hectoliter.
     * 100 liters, used for bulk liquids.
     * Peppol/UN/ECE Code: HLT
     */
    HECTOLITER("HLT", "Hectoliter"),

    /**
     * Cubic meter.
     * Used for volume measurements.
     * Peppol/UN/ECE Code: MTQ
     */
    CUBIC_METER("MTQ", "Cubic meter"),

    /**
     * Cubic centimeter.
     * Used for small volume measurements.
     * Peppol/UN/ECE Code: CMQ
     */
    CUBIC_CENTIMETER("CMQ", "Cubic centimeter"),

    /**
     * Gallon (US).
     * Used in US for liquids.
     * Peppol/UN/ECE Code: GLL
     */
    GALLON_US("GLL", "Gallon (US)"),

    /**
     * Gallon (UK).
     * Used in UK for liquids.
     * Peppol/UN/ECE Code: GLI
     */
    GALLON_UK("GLI", "Gallon (UK)"),

    // ===== LENGTH UNITS =====

    /**
     * Meter.
     * Used for length measurements.
     * Peppol/UN/ECE Code: MTR
     */
    METER("MTR", "Meter"),

    /**
     * Centimeter.
     * Used for small length measurements.
     * Peppol/UN/ECE Code: CMT
     */
    CENTIMETER("CMT", "Centimeter"),

    /**
     * Millimeter.
     * Used for very small length measurements.
     * Peppol/UN/ECE Code: MMT
     */
    MILLIMETER("MMT", "Millimeter"),

    /**
     * Kilometer.
     * Used for long distances.
     * Peppol/UN/ECE Code: KMT
     */
    KILOMETER("KMT", "Kilometer"),

    /**
     * Inch.
     * Used in imperial/US systems.
     * Peppol/UN/ECE Code: INH
     */
    INCH("INH", "Inch"),

    /**
     * Foot.
     * Used in imperial/US systems.
     * Peppol/UN/ECE Code: FOT
     */
    FOOT("FOT", "Foot"),

    /**
     * Yard.
     * Used in imperial/US systems.
     * Peppol/UN/ECE Code: YRD
     */
    YARD("YRD", "Yard"),

    /**
     * Mile.
     * Used for long distances in imperial/US systems.
     * Peppol/UN/ECE Code: SMI
     */
    MILE("SMI", "Mile"),

    // ===== AREA UNITS =====

    /**
     * Square meter.
     * Used for area measurements.
     * Peppol/UN/ECE Code: MTK
     */
    SQUARE_METER("MTK", "Square meter"),

    /**
     * Square centimeter.
     * Used for small area measurements.
     * Peppol/UN/ECE Code: CMK
     */
    SQUARE_CENTIMETER("CMK", "Square centimeter"),

    /**
     * Square foot.
     * Used in imperial/US systems.
     * Peppol/UN/ECE Code: FTK
     */
    SQUARE_FOOT("FTK", "Square foot"),

    /**
     * Square inch.
     * Used in imperial/US systems.
     * Peppol/UN/ECE Code: INK
     */
    SQUARE_INCH("INK", "Square inch"),

    // ===== TIME UNITS =====

    /**
     * Second.
     * Used for very short time measurements.
     * Peppol/UN/ECE Code: SEC
     */
    SECOND("SEC", "Second"),

    /**
     * Minute.
     * Used for short time-based services.
     * Peppol/UN/ECE Code: MIN
     */
    MINUTE("MIN", "Minute"),

    /**
     * Hour.
     * Used for time-based services (consulting, labor).
     * Peppol/UN/ECE Code: HUR
     */
    HOUR("HUR", "Hour"),

    /**
     * Day.
     * Used for daily rentals or services.
     * Peppol/UN/ECE Code: DAY
     */
    DAY("DAY", "Day"),

    /**
     * Week.
     * Used for weekly rentals or subscriptions.
     * Peppol/UN/ECE Code: WEE
     */
    WEEK("WEE", "Week"),

    /**
     * Month.
     * Used for monthly subscriptions or rentals.
     * Peppol/UN/ECE Code: MON
     */
    MONTH("MON", "Month"),

    /**
     * Year.
     * Used for annual subscriptions or services.
     * Peppol/UN/ECE Code: ANN
     */
    YEAR("ANN", "Year"),

    // ===== PACKAGING UNITS =====

    /**
     * Set.
     * Used for grouped items sold as a set.
     * Peppol/UN/ECE Code: SET
     */
    SET("SET", "Set"),

    /**
     * Pair.
     * Used for items sold in pairs (e.g., shoes, gloves).
     * Peppol/UN/ECE Code: PR
     */
    PAIR("PR", "Pair"),

    /**
     * Dozen.
     * 12 items.
     * Peppol/UN/ECE Code: DZN
     */
    DOZEN("DZN", "Dozen"),

    /**
     * Piece.
     * Used for individual items in packaging context.
     * Peppol/UN/ECE Code: NMP
     */
    PIECE("NMP", "Piece"),

    /**
     * Pack.
     * Used for packaged items.
     * Peppol/UN/ECE Code: NMP
     */
    PACK("NMP", "Pack"),

    /**
     * Box.
     * Used for boxed items.
     * Peppol/UN/ECE Code: BX
     */
    BOX("BX", "Box"),

    /**
     * Carton.
     * Used for items sold by the carton.
     * Peppol/UN/ECE Code: CT
     */
    CARTON("CT", "Carton"),

    /**
     * Case.
     * Used for cased items.
     * Peppol/UN/ECE Code: CS
     */
    CASE("CS", "Case"),

    /**
     * Package.
     * Used for packaged items.
     * Peppol/UN/ECE Code: PK
     */
    PACKAGE("PK", "Package"),

    /**
     * Pallet.
     * Used for palletized goods.
     * Peppol/UN/ECE Code: PF
     */
    PALLET("PF", "Pallet"),

    /**
     * Bottle.
     * Used for bottled products.
     * Peppol/UN/ECE Code: BO
     */
    BOTTLE("BO", "Bottle"),

    /**
     * Can.
     * Used for canned products.
     * Peppol/UN/ECE Code: CA
     */
    CAN("CA", "Can"),

    /**
     * Bag.
     * Used for bagged items.
     * Peppol/UN/ECE Code: BG
     */
    BAG("BG", "Bag"),

    // ===== SERVICE/ACTIVITY UNITS =====

    /**
     * Service unit.
     * Used for general service charges.
     * Peppol/UN/ECE Code: E48
     */
    SERVICE_UNIT("E48", "Service unit"),

    /**
     * Activity.
     * Used for activity-based billing.
     * Peppol/UN/ECE Code: ACT
     */
    ACTIVITY("ACT", "Activity"),

    /**
     * Job.
     * Used for job-based billing.
     * Peppol/UN/ECE Code: E51
     */
    JOB("E51", "Job"),

    // ===== INFORMATION/DATA UNITS =====

    /**
     * Byte.
     * Used for data storage/transfer.
     * Peppol/UN/ECE Code: AD
     */
    BYTE("AD", "Byte"),

    /**
     * Kilobyte.
     * 1000 bytes.
     * Peppol/UN/ECE Code: 2P
     */
    KILOBYTE("2P", "Kilobyte"),

    /**
     * Megabyte.
     * 1000000 bytes.
     * Peppol/UN/ECE Code: 4L
     */
    MEGABYTE("4L", "Megabyte");

    private final String code;
    private final String description;

    UnitCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Gets the UN/ECE code for this unit.
     * This is the value that will be serialized to JSON.
     *
     * @return The UN/ECE unit code
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * Gets the human-readable description of this unit.
     *
     * @return The unit description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Finds a UnitCode by its UN/ECE code.
     * Used for JSON deserialization.
     *
     * @param code The UN/ECE code to look up
     * @return The matching UnitCode
     * @throws IllegalArgumentException if the code is not recognized
     */
    @JsonCreator
    public static UnitCode fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Unit code cannot be null");
        }
        for (UnitCode unit : values()) {
            if (unit.code.equalsIgnoreCase(code)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown unit code: " + code +
                ". See UN/ECE Recommendation 20 for valid codes.");
    }

    @Override
    public String toString() {
        return code + " (" + description + ")";
    }
}