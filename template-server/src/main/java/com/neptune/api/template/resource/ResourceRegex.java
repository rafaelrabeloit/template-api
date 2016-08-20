package com.neptune.api.template.resource;

/**
 * Regex for Resources
 * 
 * @author Rafael
 *
 */
public class ResourceRegex {

    /**
     * UUID regex
     */
    public static final String UUID = "[\\p{XDigit}]{8}"
            + "(-[\\p{XDigit}]{4}){3}" + "-[\\p{XDigit}]{12}";
}
