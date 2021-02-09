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

	If you have questions or concerns about this site direct your questions to
	us via the <a href="contact">contact</a> page. Thanks!
</p>

<h2>Technology</h2>

<p> This tool is built by Dwight Frye using the <i>Svelte</i> JavaScript
framework. The back-end is built using the Java <i>DropWizard</i> restful
server framework. Both are delivered as a unit in a single <i>Docker</i>
container. The site is integrated with the Slack system to provide notifications
and contacts. </p>

<hr> <p><strong>If you have problems with the system, or suggestions for new
functionality contact us via the <a href="contact">contact</a> page
so your input can be considered.</strong></p>
