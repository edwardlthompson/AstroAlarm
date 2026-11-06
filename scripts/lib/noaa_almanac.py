"""NOAA Solar Calculator equations — published almanac reference for accuracy gates.

Source: NOAA Global Monitoring Laboratory solar equations
https://gml.noaa.gov/grad/solcalc/solareqns.PDF
"""
from __future__ import annotations

import math
from datetime import date, datetime, timezone


def _julian_day(y: int, m: int, d: int) -> float:
    if m <= 2:
        y -= 1
        m += 12
    a = y // 100
    b = 2 - a + a // 4
    return int(365.25 * (y + 4716)) + int(30.6001 * (m + 1)) + d + b - 1524.5


def sunrise_sunset_utc(
    day: date,
    lat: float,
    lon: float,
    *,
    zenith: float = 90.833,
) -> tuple[datetime | None, datetime | None]:
    """Return (sunrise_utc, sunset_utc) using NOAA equations. Lon east-positive."""
    jd = _julian_day(day.year, day.month, day.day)
    jc = (jd - 2451545.0) / 36525.0
    geom_mean_long = (280.46646 + jc * (36000.76983 + 0.0003032 * jc)) % 360
    geom_mean_anom = 357.52911 + jc * (35999.05029 - 0.0001537 * jc)
    eccent = 0.016708634 - jc * (0.000042037 + 0.0000001267 * jc)
    sun_eq = (
        math.sin(math.radians(geom_mean_anom))
        * (1.914602 - jc * (0.004817 + 0.000014 * jc))
        + math.sin(math.radians(2 * geom_mean_anom)) * (0.019993 - 0.000101 * jc)
        + math.sin(math.radians(3 * geom_mean_anom)) * 0.000289
    )
    sun_true_long = geom_mean_long + sun_eq
    sun_app_long = sun_true_long - 0.00569 - 0.00478 * math.sin(math.radians(125.04 - 1934.136 * jc))
    mean_obliq = (
        23
        + (26 + (21.448 - jc * (46.815 + jc * (0.00059 - jc * 0.001813))) / 60) / 60
    )
    obliq = mean_obliq + 0.00256 * math.cos(math.radians(125.04 - 1934.136 * jc))
    decl = math.degrees(
        math.asin(math.sin(math.radians(obliq)) * math.sin(math.radians(sun_app_long)))
    )
    y = math.tan(math.radians(obliq / 2)) ** 2
    eq_time = 4 * math.degrees(
        y * math.sin(2 * math.radians(geom_mean_long))
        - 2 * eccent * math.sin(math.radians(geom_mean_anom))
        + 4 * eccent * y * math.sin(math.radians(geom_mean_anom)) * math.cos(2 * math.radians(geom_mean_long))
        - 0.5 * y * y * math.sin(4 * math.radians(geom_mean_long))
        - 1.25 * eccent * eccent * math.sin(2 * math.radians(geom_mean_anom))
    )
    ha_arg = (
        math.cos(math.radians(zenith))
        / (math.cos(math.radians(lat)) * math.cos(math.radians(decl)))
        - math.tan(math.radians(lat)) * math.tan(math.radians(decl))
    )
    if ha_arg < -1 or ha_arg > 1:
        return None, None
    ha = math.degrees(math.acos(ha_arg))
    noon_min = 720 - 4 * lon - eq_time
    rise_min = noon_min - ha * 4
    set_min = noon_min + ha * 4

    def to_dt(mins: float) -> datetime:
        total = int(round(mins * 60))
        return datetime(day.year, day.month, day.day, tzinfo=timezone.utc) + __import__(
            "datetime"
        ).timedelta(seconds=total)

    return to_dt(rise_min), to_dt(set_min)
