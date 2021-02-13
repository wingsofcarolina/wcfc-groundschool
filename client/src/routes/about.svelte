<script>
	import { onMount } from 'svelte';
	import { goto } from '@sapper/app';

	onMount(function() {
	});
</script>

<svelte:head>
	<title>About</title>
</svelte:head>

<div class=title>About</div>
<hr class="highlight">

<center>
<div class="narrow">
	<p> This site has been put in place to facilitate the sharing of ground school
		materials at the Wings of Carolina Flying Club. Moving to the remote teaching
		format necessitated by the COVID-19 pandemic has made distributing materials
		somewhat more challenging. To ensure that every student has the most current
		version of any given handout it was determined that the most effective approach
		would be to provide a centralized distribution website. This is it.

		If you have questions or concerns about this site direct your questions to
		us via the <a href="contact">contact</a> page. Thanks!
	</p>

	<h2>Cookies</h2>

	<p> This site uses two cookies, and one piece of local storage. One cookie is a
	simple tracking cookie used to indicate that the browser has touched the site at
	least once in the past. Another cookie is used to store a cryptographically
	secure login token identifying the user to the site. The one local storage item
	indicates whether the user has seen the cookie usage banner as is now required
	by the <a target='_blank' href='https://www.cookielaw.org/the-cookie-law/'>
	European Union</a>. </p>

	<p> <b>NOTE</b> : By continuing to use this site you implicitly agree to the use
	of this storage  on your device. </p>

	<h2>Technology</h2>

	<p> This tool is built by Dwight Frye using the <i>Svelte</i> JavaScript
	framework. The back-end is built using the Java <i>DropWizard</i> restful
	server framework. Both are delivered as a unit in a single <i>Docker</i>
	container. The site is integrated with the Slack system to provide notifications
	and contacts. </p>

	<hr> <p><strong>If you have problems with the system, or suggestions for new
	functionality contact us via the <a href="contact">contact</a> page
	so your input can be considered.</strong></p>
</div>
</center>

<style>
.title {
  font-size: 2em;
  text-align: center;
}
.highlight {
  height: 4px;
  margin-top: 25px;
  margin-bottom: 40px;
  width: 250px;
  border-color: rgb(40, 90, 149);
  background-color: rgb(40, 90, 149);
  border-radius: 3px;
	margin: 0px auto 50px auto;
}
.narrow p {
	width: 70%;
	text-align: left;
	font-size: 1.2em;
}
</style>
