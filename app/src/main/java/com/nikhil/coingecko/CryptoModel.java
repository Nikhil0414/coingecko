package com.nikhil.coingecko;

// CryptoModel.java
    public class CryptoModel {
        private int id;
        private String name;
        private String symbol;
        private Quote quote;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSymbol() {
            return symbol;
        }

        public Quote getQuote() {
            return quote;
        }

        public static class Quote {
            private USD USD;

            public USD getUSD() {
                return USD;
            }

            public static class USD {
                private double price;

                public double getPrice() {
                    return price;
                }
            }
        }
    }
