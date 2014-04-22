# quotation-portal

This is a mock-up quotation and policy management portal for life insurance products. The application enables premium calculation and state management of insurance policies. 

The premium calculation is done based on the input given in the application form and the mortality tables located in the q.xlsx file. The premium calculation implements formulas of actuarial mathematics using clojure's rich collection manipulation functions. The actuarial formulas implemented in the application are given in the quotation-portal-actuarial.pdf file.

In the application, insurance policies are persisted to a sqlite database.

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## Clojure

The used version of clojure is 1.5.1.

The application is built based on leiningen's compojure-app template.

## Clojure libraries

### compojure 1.1.6
A concise routing library for Ring
	[compojure "1.1.6"]

### hiccup 1.0.5

	[hiccup "1.0.5"]

### ring-server 0.3.1

	[ring-server "0.3.1"]

### math.numeric-tower 0.0.2

	[org.clojure/math.numeric-tower "0.0.2"]

### clj-time 0.6.0

	[clj-time "0.6.0"]

### java.jdbc 0.2.3

	[org.clojure/java.jdbc "0.2.3"]

### sqlite-jdbc 3.7.2

	[org.xerial/sqlite-jdbc "3.7.2"]

## Notes

The application is built for educational purposes.

## License

Copyright © 2014 Igor Munjic

Distributed under the Eclipse Public License, the same as Clojure.
