<script>
	import { onMount } from 'svelte'
	import { goto } from '$app/navigation'
	import * as notifier from '@beyonk/svelte-notifications/src/notifier.js'
	import { user } from '$lib/store.js'
	import { getUser } from '$lib/common.js'
	import Section from "$lib/components/Section.svelte";
	import Handout from "$lib/components/Handout.svelte";

	var data = null;

	onMount(async () => {
		getUser();
		cookieWarning();
	});

	function cookieWarning() {
		if (localStorage.getItem('cookieSeen') != 'shown') {
			var el = document.getElementById('cookie-banner');
			if (el) el.style.visibility = "visible";
			localStorage.setItem('cookieSeen', 'shown')
		}
	}

	function acceptCookie() {
		 var el = document.getElementById('cookie-banner');
		 //if (el) el.style.visibility = "hidden";
		 var fadeEffect = setInterval(function() {
			 if (!el.style.opacity) {
				 el.style.opacity = 1;
			 }
			 if (el.style.opacity > 0) {
				 el.style.opacity -= 0.1;
			 } else {
				 clearInterval(fadeEffect);
			 }
		 }, 150);
	 }
</script>

<svelte:head>
	<title>WCFC Ground School</title>
</svelte:head>

	<div class="outer">
		<div class="inner">
			<div class=title>Welcome!</div>
			<hr class="highlight">

			<center>
			<div class="narrow">

				<p>Here you'll find all the supporting handouts and other material for
				the WCFC Ground Schools.  </p>

				<p>To access this material you must be registered for the class and have
				had your registration information uploaded to the system. You gain
				access to the system by entering your email address, having it verified
				by the system which will send an email to your registered email address.
				That email will contain a code. That code should be entered into the
				Authenticate page, and the "Verify Code" button is clicked. If the code
				matches then you will be permitted to access the the class materials. A
				cookie will be placed on your browser identifying you so this
				authentication need only happen once per system/browser unless you clear
				your browser data. </p>

				<p> The links in the navigation bar above will take you to the various
				published class materials. If you have questions about the material use
				either the "<a href="contact">contact</a>" page referenced above in the
				navigation bar or go into the Slack workspace and ask your questions
				there. </p>

				<p>You may bookmark your specific class page and can jump directly to
				those materials when you wish, for your convenience. </p>

			</div>
		  </center>
		</div>
	</div>

	<div id='cookie-banner' class='cookie-banner'>
	<p>
			By using this website, you agree to our
			<a href='about'>cookie policy</a>
		</p>
		<button class='close' on:click={acceptCookie.bind()}>&times;</button>
	</div>

<style>
button {
	text-align: center;
}
.narrow p {
	width: 70%;
	text-align: left;
}
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
.outer {
  display: flex;
	flex-direction: column;
	align-items: center;
}
.inner {
	display: flex;
	flex-direction: column;
}
@media (min-width: 480px) {
}
.cookie-banner {
	visibility: hidden;
	position: fixed;
  bottom: 60px;
  left: 10%;
  right: 10%;
  width: 80%;
  padding: 5px 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #eee;
  border-radius: 5px;
  box-shadow: 0 0 2px 1px rgba(0, 0, 0, 0.2);
}
.close {
  height: 20px;
  background-color: #777;
  border: none;
  color: white;
  border-radius: 2px;
  cursor: pointer;
}
</style>
