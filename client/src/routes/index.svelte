<script>
	import { onMount } from 'svelte'
	import { goto } from '@sapper/app'
	import { notifier } from '@beyonk/svelte-notifications'
	import { user } from '../store.js'
	import Section from "../components/Section.svelte";
	import Handout from "../components/Handout.svelte";

	var data = null;

	onMount(async () => {
		getUser();
		cookieWarning();
	});

	const mockUser = async () => {
		const response = await fetch('/api/mock', {
			method: "get",
			withCredentials: true,
			headers: {
				'Accept': 'application/json'
			}
		});
		console.log("Mock response : ", response);
		if (!response.ok) {
			if (response.status == 404) {
				// User was simpoly not found, therefore not authenticated
				user.set(null);
			} else {
				// Otherwise, something else went wrong
				notifier.danger('Retrieve of mock failed.');
			}
		}
	}

	const getUser = async () => {
		const response = await fetch('/api/user', {
			method: "get",
			withCredentials: true,
			headers: {
				'Accept': 'application/json'
			}
		});
		if (!response.ok) {
			if (response.status == 404) {
				// User was simpoly not found, therefore not authenticated
				user.set(null);
			} else {
				// Otherwise, something else went wrong
				notifier.danger('Retrieve of user information failed.');
			}
		} else {
			var tmp = await response.json();
			user.set(tmp);
		}
	}

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

				<p>We are making this information available through this site to support
				our online classes but if this proves useful we will continue when we
				return to the classroom. </p>

				<p>To access this material you will be asked to join the
				WCFC-Groundschool Slack workspace (for more information on Slack go to
				slack.com) since this website uses Slack to authenticate that you are a
				registered member of a groundschool class. That workspace will also be
				used to communicate and coordinate between the students and those
				managing the class. </p>

				<p>The links in the navigation bar above will take you to the various
				published class materials. If you have questions about the material use
				either the "contact" page referenced above in the navigation bar or go
				into the Slack workspace and ask your questions there. </p>

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
	font-size: 1.2em;
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
