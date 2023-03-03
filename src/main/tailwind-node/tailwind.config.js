/** @type {import('tailwindcss').Config} **/
module.exports = {
  content: [
	    "../resources/templates/*.html",
	],
  theme: {
      extend: {
	  boxShadow: {
              'book-shadow': '0px 0px 9px 4px rgba(0, 0, 0, 0.3)',
      }
    }
  },
  plugins: [],
}
