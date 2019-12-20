/**
 * @author Tabassum Fabiha | tf2478
 * 
 * This class holds all the different types of plays possible in RPSKL and also holds how
 * each play is represented in the UI.
 */
public enum RPSKL {
	ROCK {
		/**
		 * @override
		 */
		public String toString() {
	          return "rOc"; // rocks are bumpy
	    }
	},
	PAPER {
		/**
		 * @override
		 */
		public String toString() {
	          return "PAPER"; // papers are straight and uniform
	    }
	},
	SCISSORS {
		/**
		 * @override
		 */
		public String toString() {
	          return "scisSORS"; // scissors widen outward
	    }
	},
	SPOCK {
		/**
		 * @override
		 */
		public String toString() {
	          return "Spock"; // Spock is a person so his name is spelled like a person's
	    }
	},
	LIZARD {
		/**
		 * @override
		 */
		public String toString() {
	          return "lizzzzard"; // lizards are long
	    }
	};
}
