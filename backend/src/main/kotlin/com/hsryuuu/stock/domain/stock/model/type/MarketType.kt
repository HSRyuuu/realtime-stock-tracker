package com.hsryuuu.stock.domain.stock.model.type

enum class MarketType(val engName: String, val korName: String) {
    // 🇺🇸 미국
    NASDAQ("Nasdaq Stock Market", "나스닥"),
    NYSE("New York Stock Exchange", "뉴욕증권거래소"),
    CBOE("Cboe Global Markets", "시카고옵션거래소"),
    OTC("OTC Markets", "미국 장외시장"),

    // 🇰🇷 한국
    KRX("Korea Exchange", "한국거래소"),
    KOSDAQ("KOSDAQ (part of KRX)", "코스닥"),
    KONEX("KONEX (part of KRX)", "코넥스"),

    // 🇨🇳 중국
    SSE("Shanghai Stock Exchange", "상하이 증권거래소"),
    SZSE("Shenzhen Stock Exchange", "선전 증권거래소"),

    // 🇭🇰 홍콩
    HKEX("Hong Kong Exchange", "홍콩 증권거래소"),

    // 🇯🇵 일본
    JPX("Japan Exchange Group (Tokyo Stock Exchange)", "도쿄 증권거래소"),

    // 🇹🇼 대만
    TWSE("Taiwan Stock Exchange", "대만 증권거래소"),

    // 🇲🇾 말레이시아
    MYX("Bursa Malaysia", "말레이시아 증권거래소"),

    // 🇸🇬 싱가포르
    SGX("Singapore Exchange", "싱가포르 증권거래소"),

    // 🇦🇺 호주
    ASX("Australian Securities Exchange", "호주 증권거래소"),

    // 🇹🇭 태국
    SET("Stock Exchange of Thailand", "태국 증권거래소"),

    // 🇵🇭 필리핀
    PSE("Philippine Stock Exchange", "필리핀 증권거래소"),

    // 🇮🇩 인도네시아
    IDX("Indonesia Stock Exchange", "인도네시아 증권거래소"),

    // 🇳🇿 뉴질랜드
    NZX("New Zealand Exchange", "뉴질랜드 증권거래소");

    companion object {
        fun fromCode(code: String): MarketType? =
            entries.firstOrNull { it.name.equals(code, ignoreCase = true) }
    }
}