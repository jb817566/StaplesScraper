# StaplesScraper
Scrape the Staples website for product data from a starting SKU#

Command-line Utility that accepts a starting integer value from which to derive SKU numbers to grab:
SKU, Product Name, Price, Stock Status

Features:
Outputs to simple CSV format for accessibility in raw text format to inspect.
Uses Selenium Webdriver for robust element search and timeouts in order to allow for dynamic content
Prints to stdout for status messages. Low on resources and simple.

Notes:
Has no ability to update the CSV.
