package org.astroalarm.astro.place

/** Bundled major-city gazetteer for offline search. Geocoder is a fallback only. */
object CityCatalog {
    data class Entry(val name: String, val lat: Double, val lon: Double, val tz: String)

    private val cities = listOf(
        Entry("Amsterdam", 52.3676, 4.9041, "Europe/Amsterdam"),
        Entry("Athens", 37.9838, 23.7275, "Europe/Athens"),
        Entry("Auckland", -36.8509, 174.7645, "Pacific/Auckland"),
        Entry("Bangkok", 13.7563, 100.5018, "Asia/Bangkok"),
        Entry("Barcelona", 41.3874, 2.1686, "Europe/Madrid"),
        Entry("Beijing", 39.9042, 116.4074, "Asia/Shanghai"),
        Entry("Berlin", 52.5200, 13.4050, "Europe/Berlin"),
        Entry("Bogota", 4.7110, -74.0721, "America/Bogota"),
        Entry("Boston", 42.3601, -71.0589, "America/New_York"),
        Entry("Buenos Aires", -34.6037, -58.3816, "America/Argentina/Buenos_Aires"),
        Entry("Cairo", 30.0444, 31.2357, "Africa/Cairo"),
        Entry("Cape Town", -33.9249, 18.4241, "Africa/Johannesburg"),
        Entry("Chicago", 41.8781, -87.6298, "America/Chicago"),
        Entry("Copenhagen", 55.6761, 12.5683, "Europe/Copenhagen"),
        Entry("Delhi", 28.6139, 77.2090, "Asia/Kolkata"),
        Entry("Denver", 39.7392, -104.9903, "America/Denver"),
        Entry("Dubai", 25.2048, 55.2708, "Asia/Dubai"),
        Entry("Dublin", 53.3498, -6.2603, "Europe/Dublin"),
        Entry("Helsinki", 60.1699, 24.9384, "Europe/Helsinki"),
        Entry("Hong Kong", 22.3193, 114.1694, "Asia/Hong_Kong"),
        Entry("Istanbul", 41.0082, 28.9784, "Europe/Istanbul"),
        Entry("Jakarta", -6.2088, 106.8456, "Asia/Jakarta"),
        Entry("Johannesburg", -26.2041, 28.0473, "Africa/Johannesburg"),
        Entry("Lisbon", 38.7223, -9.1393, "Europe/Lisbon"),
        Entry("London", 51.5074, -0.1278, "Europe/London"),
        Entry("Los Angeles", 34.0522, -118.2437, "America/Los_Angeles"),
        Entry("Madrid", 40.4168, -3.7038, "Europe/Madrid"),
        Entry("Mexico City", 19.4326, -99.1332, "America/Mexico_City"),
        Entry("Miami", 25.7617, -80.1918, "America/New_York"),
        Entry("Montreal", 45.5017, -73.5673, "America/Toronto"),
        Entry("Moscow", 55.7558, 37.6173, "Europe/Moscow"),
        Entry("Mumbai", 19.0760, 72.8777, "Asia/Kolkata"),
        Entry("Nairobi", -1.2921, 36.8219, "Africa/Nairobi"),
        Entry("New York", 40.7128, -74.0060, "America/New_York"),
        Entry("Oslo", 59.9139, 10.7522, "Europe/Oslo"),
        Entry("Paris", 48.8566, 2.3522, "Europe/Paris"),
        Entry("Reykjavik", 64.1466, -21.9426, "Atlantic/Reykjavik"),
        Entry("Rio de Janeiro", -22.9068, -43.1729, "America/Sao_Paulo"),
        Entry("Rome", 41.9028, 12.4964, "Europe/Rome"),
        Entry("San Francisco", 37.7749, -122.4194, "America/Los_Angeles"),
        Entry("Santiago", -33.4489, -70.6693, "America/Santiago"),
        Entry("Sao Paulo", -23.5505, -46.6333, "America/Sao_Paulo"),
        Entry("Seattle", 47.6062, -122.3321, "America/Los_Angeles"),
        Entry("Seoul", 37.5665, 126.9780, "Asia/Seoul"),
        Entry("Shanghai", 31.2304, 121.4737, "Asia/Shanghai"),
        Entry("Singapore", 1.3521, 103.8198, "Asia/Singapore"),
        Entry("Stockholm", 59.3293, 18.0686, "Europe/Stockholm"),
        Entry("Sydney", -33.8688, 151.2093, "Australia/Sydney"),
        Entry("Tokyo", 35.6762, 139.6503, "Asia/Tokyo"),
        Entry("Toronto", 43.6532, -79.3832, "America/Toronto"),
        Entry("Vancouver", 49.2827, -123.1207, "America/Vancouver"),
        Entry("Vienna", 48.2082, 16.3738, "Europe/Vienna"),
        Entry("Warsaw", 52.2297, 21.0122, "Europe/Warsaw"),
        Entry("Washington", 38.9072, -77.0369, "America/New_York"),
        Entry("Zurich", 47.3769, 8.5417, "Europe/Zurich"),
    )

    fun search(query: String, maxResults: Int = 5): List<AstroPlace> {
        val needle = query.trim().lowercase()
        if (needle.length < 2) return emptyList()
        return cities
            .filter { it.name.lowercase().contains(needle) }
            .take(maxResults)
            .map { AstroPlace(it.name, it.lat, it.lon, it.tz) }
    }
}
