package com.example.application.util;

import com.example.application.model.Location;

public final class ShippingCalculator {
    private static final long RATE_PER_SEGMENT = 500L;
    private static final int SEGMENT_IN_METERS = 10;
    private static final long COST_PER_METER = RATE_PER_SEGMENT / SEGMENT_IN_METERS;

    private ShippingCalculator() {
    }

    /**
     * Hitung jarak manhattan (|x2 - x1| + |y2 - y1|).
     */
    public static int manhattanDistance(Location from, Location to) {
        if (from == null || to == null) {
            return 0;
        }
        return Math.abs(from.getCoordinateX() - to.getCoordinateX())
                + Math.abs(from.getCoordinateY() - to.getCoordinateY());
    }

    /**
     * Hitung ongkir sebelum pembulatan: distance * 50 (karena 500 per 10 meter).
     */
    public static long calculateBaseCost(int distanceMeters) {
        if (distanceMeters <= 0) {
            return 0L;
        }
        return distanceMeters * COST_PER_METER;
    }

    /**
     * Bulatkan ke 500 terdekat.
     */
    public static long roundToNearest500(long amount) {
        if (amount <= 0) {
            return 0L;
        }
        return Math.round(amount / (double) RATE_PER_SEGMENT) * RATE_PER_SEGMENT;
    }

    public static ShippingInfo calculate(Location from, Location to) {
        int distance = manhattanDistance(from, to);
        long baseCost = calculateBaseCost(distance);
        long roundedCost = roundToNearest500(baseCost);
        return new ShippingInfo(distance, baseCost, roundedCost);
    }

    public record ShippingInfo(int distanceMeters, long baseCost, long finalCost) {
    }
}
