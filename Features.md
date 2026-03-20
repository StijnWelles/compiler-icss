# Parsing
- [x] PA00 - Gebruik maken van eigen `Stack<T>` implementatie
- [x] PA01 - Parsen van level0 > Eenvoudige taal
- [x] PA02 - Parsen van level1 > Toewijzen en gebruik van variabelen
	- [ ] Parsen van variabelen binnen een andere scope en de scope opslaan
- [x] PA03 - Parsen van level2 > Plus, min, keer operations
- [x] PA04 - Parsen van level3 > If/else
- [ ] Hex kleuren notatie parsen volgens [de standard](https://www.w3.org/TR/css-color-4/#hex-notation), de mogelijke opties zijn:
	- 6 getallen `#00ff00` (komt overeen met `rgb(0 255 0)`)
	- 8 getallen `#0000ffcc` (komt overeen met `rgb(0 255 0)` met 80% transparantie)
	- 3 getallen `#f00` (komt overeen met `#ff0000`)
	- 4 getallen `#00fc` (komt overeen met `#0000ffcc`)
- [ ] RGB kleuren parsen
- [ ] Meerdere selectors kunnen meegeven (Bijv `.a, #b {...}` selecteert class a en id b)
- [ ] Scalar: Kommagetallen

# Checking
- [x] CH01 - Error bij gebruik van niet-gedefinieerde variabelen
- [x] CH02 - Operation regels: geen pixels/percentages mixen, bij plus/min moeten beide hetzelfde zijn, bij keer mag er maximaal 1 non-scalar value zijn.
- [x] CH03 - Geen kleuren in operations
- [x] CH04 - Controleren of de juiste type wordt gebruikt bij de properties
- [ ] CH05 - Controleren of de condition bij een if clause type boolean is
- [ ] CH06 - Controleren of variabelen binnen de scope worden gebruikt

# Transforming
- [ ] TR01 - Expressions evalueren, alle Operations vervangen met een Literal
- [ ] TR02 - If/else evalueren, juiste body selecteren op basis van de waarde van de boolean.
- [ ] Optimalisatie: dubbele statements weghalen, bijv: `height: 20px; height: 50px;` wordt `height: 50px;` omdat de 20px redundant is.

# Generating
- [ ] GE01 - Omzetten AST naar CSS2-compliant string
- [ ] GE02 - Per scopeniveau twee spaties toevoegen


# Bugs
- Operation in variable assignment (bv `ParWidth := 500px + 5px;`) werkt niet