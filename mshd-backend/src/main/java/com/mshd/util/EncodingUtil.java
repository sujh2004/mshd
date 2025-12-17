package com.mshd.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MSHD一体化编码工具类
 * 编码规则：震情码(26位) + 来源码(3位) + 载体码(1位) + 灾情码(6位) = 36位
 */
public class EncodingUtil {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成完整的一体化编码
     *
     * @param geographicCode     地理信息码(12位)
     * @param earthquakeTime     地震发生时间
     * @param sourceCode         来源码(3位)
     * @param carrierCode        载体码(1位)
     * @param disasterCode       灾情码(6位)
     * @return 36位编码ID
     */
    public static String generateEncodedId(String geographicCode,
                                            LocalDateTime earthquakeTime,
                                            String sourceCode,
                                            String carrierCode,
                                            String disasterCode) {
        // 生成时间码(14位)
        String timeCode = earthquakeTime.format(TIME_FORMATTER);

        // 生成震情码(26位) = 地理信息码(12位) + 时间码(14位)
        String earthquakeCode = geographicCode + timeCode;

        // 拼接完整编码
        return earthquakeCode + sourceCode + carrierCode + disasterCode;
    }

    /**
     * 生成震情码
     *
     * @param geographicCode 地理信息码(12位)
     * @param earthquakeTime 地震发生时间
     * @return 26位震情码
     */
    public static String generateEarthquakeCode(String geographicCode, LocalDateTime earthquakeTime) {
        String timeCode = earthquakeTime.format(TIME_FORMATTER);
        return geographicCode + timeCode;
    }

    /**
     * 生成地理信息码(12位)
     * 格式：省(2位) + 市(2位) + 区县(2位) + 乡镇街道(3位) + 村/社区(3位)
     *
     * @param province   省代码
     * @param city       市代码
     * @param district   区县代码
     * @param town       乡镇代码
     * @param village    村/社区代码
     * @return 12位地理信息码
     */
    public static String generateGeographicCode(String province, String city, String district,
                                                 String town, String village) {
        return padLeft(province, 2) +
               padLeft(city, 2) +
               padLeft(district, 2) +
               padLeft(town, 3) +
               padLeft(village, 3);
    }

    /**
     * 生成时间码(14位)
     * 格式：YYYYMMDDHHmmss
     *
     * @param dateTime 日期时间
     * @return 14位时间码
     */
    public static String generateTimeCode(LocalDateTime dateTime) {
        return dateTime.format(TIME_FORMATTER);
    }

    /**
     * 生成来源码(3位)
     * 第一段：大类代码(1位)
     * 第二段：子类代码(2位)
     *
     * @param categoryCode 大类代码 (1: 业务报送, 2: 泛在感知, 3: 其他)
     * @param subCode      子类代码
     * @return 3位来源码
     */
    public static String generateSourceCode(String categoryCode, String subCode) {
        return categoryCode + padLeft(subCode, 2);
    }

    /**
     * 生成载体码(1位)
     * 0: 文字, 1: 图像, 2: 音频, 3: 视频, 4: 其他
     *
     * @param carrierType 载体类型
     * @return 1位载体码
     */
    public static String generateCarrierCode(String carrierType) {
        switch (carrierType.toLowerCase()) {
            case "text":
            case "文字":
                return "0";
            case "image":
            case "图像":
                return "1";
            case "audio":
            case "音频":
                return "2";
            case "video":
            case "视频":
                return "3";
            default:
                return "4";
        }
    }

    /**
     * 生成灾情码(6位)
     * 格式：灾害大类(1位) + 灾害子类(2位) + 灾情指标(3位)
     *
     * @param category      灾害大类代码
     * @param subcategory   灾害子类代码
     * @param indicator     灾情指标代码
     * @return 6位灾情码
     */
    public static String generateDisasterCode(String category, String subcategory, String indicator) {
        return category + padLeft(subcategory, 2) + padLeft(indicator, 3);
    }

    /**
     * 解析编码ID
     *
     * @param encodedId 36位编码ID
     * @return 解析结果对象
     */
    public static EncodingResult parseEncodedId(String encodedId) {
        if (StringUtils.isBlank(encodedId) || encodedId.length() != 36) {
            throw new IllegalArgumentException("Invalid encoded ID: " + encodedId);
        }

        EncodingResult result = new EncodingResult();

        // 解析震情码(前26位)
        String earthquakeCode = encodedId.substring(0, 26);
        result.setEarthquakeCode(earthquakeCode);

        // 解析地理信息码(前12位)
        String geographicCode = encodedId.substring(0, 12);
        result.setGeographicCode(geographicCode);

        // 解析时间码(13-26位)
        String timeCode = encodedId.substring(12, 26);
        result.setTimeCode(timeCode);

        // 解析来源码(27-29位)
        String sourceCode = encodedId.substring(26, 29);
        result.setSourceCode(sourceCode);

        // 解析载体码(30位)
        String carrierCode = encodedId.substring(29, 30);
        result.setCarrierCode(carrierCode);

        // 解析灾情码(31-36位)
        String disasterCode = encodedId.substring(30, 36);
        result.setDisasterCode(disasterCode);

        // 解析灾害大类(31位)
        result.setDisasterCategory(encodedId.substring(30, 31));

        // 解析灾害子类(32-33位)
        result.setDisasterSubcategory(encodedId.substring(31, 33));

        // 解析灾情指标(34-36位)
        result.setIndicatorCode(encodedId.substring(33, 36));

        return result;
    }

    /**
     * 左补零
     */
    private static String padLeft(String str, int length) {
        return StringUtils.leftPad(str, length, '0');
    }

    /**
     * 编码解析结果类
     */
    public static class EncodingResult {
        private String earthquakeCode;      // 震情码(26位)
        private String geographicCode;      // 地理信息码(12位)
        private String timeCode;            // 时间码(14位)
        private String sourceCode;          // 来源码(3位)
        private String carrierCode;         // 载体码(1位)
        private String disasterCode;        // 灾情码(6位)
        private String disasterCategory;    // 灾害大类(1位)
        private String disasterSubcategory; // 灾害子类(2位)
        private String indicatorCode;       // 灾情指标(3位)

        // Getters and Setters
        public String getEarthquakeCode() { return earthquakeCode; }
        public void setEarthquakeCode(String earthquakeCode) { this.earthquakeCode = earthquakeCode; }

        public String getGeographicCode() { return geographicCode; }
        public void setGeographicCode(String geographicCode) { this.geographicCode = geographicCode; }

        public String getTimeCode() { return timeCode; }
        public void setTimeCode(String timeCode) { this.timeCode = timeCode; }

        public String getSourceCode() { return sourceCode; }
        public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }

        public String getCarrierCode() { return carrierCode; }
        public void setCarrierCode(String carrierCode) { this.carrierCode = carrierCode; }

        public String getDisasterCode() { return disasterCode; }
        public void setDisasterCode(String disasterCode) { this.disasterCode = disasterCode; }

        public String getDisasterCategory() { return disasterCategory; }
        public void setDisasterCategory(String disasterCategory) { this.disasterCategory = disasterCategory; }

        public String getDisasterSubcategory() { return disasterSubcategory; }
        public void setDisasterSubcategory(String disasterSubcategory) { this.disasterSubcategory = disasterSubcategory; }

        public String getIndicatorCode() { return indicatorCode; }
        public void setIndicatorCode(String indicatorCode) { this.indicatorCode = indicatorCode; }
    }
}
