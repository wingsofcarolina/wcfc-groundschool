<script>
	import { onMount } from 'svelte'
	import { goto } from '@sapper/app'
	import { notifier } from '@beyonk/svelte-notifications'
	import Section from "../components/Section.svelte";
	import Handout from "../components/Handout.svelte";

	var data = null;

	onMount(async () => {
		cookieWarning();
		getIndex();
	});

	const getIndex = async () => {
		const response = await fetch('/api/index', {
			method: "get",
			withCredentials: true,
			headers: {
				'Accept': 'application/json'
			}
		});
		if (!response.ok) {
			notifier.danger('Retrieve of class index failed.');
		} else {
			data = await response.json();
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
	<title>WCFC Ground School Materials</title>
</svelte:head>

<div class="outer">
	<div class="inner">
		{#if data}
			{#each data.children as section}
				<Section section={section.label} items={section.children} />
			{/each}
		{/if}
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
