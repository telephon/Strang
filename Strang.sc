// String type that:
// 1. can represent an extended set of ascci characters (http://en.wikipedia.org/wiki/UTF-8)
// 2. responds to the collection methods, so is more practical in many cases.
// 3. can represent nested stings
// ("Strang" is German for string/strand)


Strang : List {

	*newFrom { |string|
		^string.asStrang
	}

	species { ^this.class }

	addAll { |string|
		^super.addAll(string.asStrang)
	}

	asStrang {
		^this
	}

	asString {
		^array.join
	}

	ascii {
		^array.ascii.flat
	}

	printOn { arg stream;
		this.asString.printOn(stream)
	}

	// some methods from String:

	compare { | aString, ignoreCase=false |
		^this.asString.compare(aString, ignoreCase)
	}
	< { arg aString; ^this.asString < aString.asString }
	<= { arg aString; ^this.asString <= aString.asString }
	> { arg aString; ^this.asString > aString.asString }
	>= { arg aString; ^this.asString >= aString.asString }

	split { |separator=$/, includeEmpty = true|
		var res = Array.new;
		var curr = this.class.new;
		array.do { |char|
			if(char == separator) {
				if(includeEmpty or: { curr.notEmpty }) {
					res = res.add(curr)
				};
				curr = this.class.new;
			} {
				curr.add(char)
			}
		};
		if(curr.notEmpty) { res = res.add(curr) };
		^res
	}

	+ { |string|
		^this ++ " " ++ string
	}

	normalize {
		^this.collect { |elem|
			if(elem.isKindOf(Char)) { elem } { elem.asStrang }
		}
	}

}


BigChar : Char {
	var chars;

	*new { |chars|
		^super.newCopyArgs(chars)
	}

	asAscii {
		^chars
	}

	ascii {
		^chars.ascii
	}

	printOn { arg stream;
		chars.do { |each| stream.put(each) }
	}

	== { arg obj;
		^this.compareObject(obj, #[\chars])
	}

	hash {
		^this.instVarHash(#[\chars])
	}
}

+ Object {
	asStrang {
		^this.asString.asStrang
	}
}

+ String {
	asStrang {
		var curr, res, counter, nonAscii;
		res = Strang.new(this.size);
		this.size.do { |i|
			// thanks to Laureano López:
			var character = this[i];
			var ascii = character.ascii;
			if (ascii < 0) {
				case
				{ (ascii >= -64) && (ascii < -32) } { counter = 2 }
				{ (ascii >= -32) && (ascii < -16) } { counter = 3 }
				{ ascii >= -16 } { counter = 4 };
				nonAscii = nonAscii.add(character);
				counter = counter - 1;
				if (counter == 0) {
					if(nonAscii.isNil) { Error("can't encode string").throw };
					res.add(BigChar(nonAscii)); // this could be Strang, then we can skip BigChar completely. But sematics?
					nonAscii = nil;
				};
			} {
				res = res.add(character);
			};
		};
		^res
	}
}

+ Integer {
	ascii {
		^this
	}
}

+ SequenceableCollection {
	asAscii {
		var res = String.new(this.size);
		this.do { |item| res.addAll(item.asAscii) };
		^res
	}
}
