<script>
	import { onMount } from 'svelte';
	import { goto } from '@sapper/app';
	import { version, server, requestReload } from '../store.js'

	onMount(function() {
	});
</script>

<svelte:head>
	<title>About</title>
</svelte:head>

<h1>About</h1>

<p> This site has been put in place to facilitate the sharing of ground school
	materials at the Wings of Carolina Flying Club. Moving to the remote teaching
	format necessitated by the COVID-19 pandemic has made distributing materials
	somewhat more challenging. To ensure that every student has the most current
	version of any given handout it was determined that the most effective approach
	would be to provide a centralized distribution website. This is it.

	If you have questions or concerns aobut this site direct your questions to
	<a href="mailto:dfrye@planez.co">Dwight Frye</a>, as he is the person who built
	the site.
</p>

<h2>Cookies</h2>

<p> This site uses no cookies (yet), and one piece of local storage. The one
local storage item indicates whether the user has seen the cookie usage banner
as is now required by the <a target='_blank'
href='https://www.cookielaw.org/the-cookie-law/'> European Union</a>. </p>

<p> <b>NOTE</b> : By continuing to use this site you implicitly agree to the use
of this storage  on your device. </p>

<h2>Technology</h2>

<p> This tool is built using the <i>Svelte</i> JavaScript framework. The
back-end is  built using the Java <i>DropWizard</i> restful server framework.
Both are delivered as a unit in a single <i>Docker</i> container. The database
is <i>MongoDB</i>, accessed from Java using the  <i>Morphia</i> library.</p>

Some icons have been provided by Icon8. They include the following icons :

<ul>
<li><a target="_blank" href="https://icons8.com/icons/set/note">Note icon</a>
		icon by <a target="_blank" href="https://icons8.com">Icons8</a>
<li><a target="_blank" href="https://icons8.com/icons/set/help">Help icon</a>
		icon by <a target="_blank" href="https://icons8.com">Icons8</a>
<li><a target="_blank" href="https://icons8.com/icons/set/realtime-protection">Realtime icon</a>
		icon by <a target="_blank" href="https://icons8.com">Icons8</a>
<li><a target="_blank" href="https://icons8.com/icons/set/refresh">Refresh icon</a>
		icon by <a target="_blank" href="https://icons8.com">Icons8</a>
</ul>

<hr> <p><strong>If you have problems with the system, or suggestions for new
functionality send mail to <a href="mailto:dfrye@planez.co">dfrye@planez.co</a>
so your input can be considered.</strong></p>

<hr>
<h2>Change Log</h2>

<h3>Changes in 0.0.1</h3>
<ul>
<li>Initial ALPHA release!</li>
</ul>
