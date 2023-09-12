<script>
	import { onMount } from 'svelte';
	import { user } from '../store.js'
	import { getUser } from '../common.js'

	onMount(function() {
		getUser();
	});

</script>

<svelte:head>
	<title>Known</title>
</svelte:head>

<div class=title>Already Logged In</div>
<hr class="highlight">

<center>
	<div class="narrow">
		<p>
			{#if $user}
			Hello <b>{$user.name}</b>, it
			{:else}
			It
			{/if}
			appears that you have clicked on the verification URL in your email
			more than once. You should understand that the verification URL sent to
			you for validation is needed only once, and clicking on it again
			will bring you to this page. </p>
		<p>The initial use of the URL caused a cookie to be placed on that instance
			of your browser. The system will from that point forward recognize you
			and allow you to use the system. That is, in fact, how it recognized your
			additional use of the URL and brought you to this informatinal page.<p>
		<p>If you need to authenticate another browser, potentially on another
			system entirely, you will need to request another authentication email and
			use the provided URL on <i>that browser</i>, and use it only once.<p>
		<p>If you are still uncertain as to how you got here, talk with one of
			the club ground school website administrators.<p>
	</div>

</center>

<style>
.auth {
	margin-top: 3em;
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
.section {
  width: 100%;
  margin-bottom: 3em;
}
.narrow p {
	width: 70%;
	text-align: left;
}
.warning {
	width: 70%;
	text-align: left;
	margin-bottom: 20px;
}
.contact_block {
  display: flex;
  justify-content: space-around;
  margin: 20px;
  font-size: 20px;
}
.contact_info {
  text-align: center;
}

input, textarea {
	font-family: 'Courier', sans-serif;
	padding: 10px;
	margin: 10px 0;
	border:0;
	box-shadow:0 0 15px 4px rgba(0,0,0,0.06);
	font-family: inherit;
}
input[type=submit] {
		padding:5px 15px;
		background:#ccc;
		border:0 none;
		cursor:pointer;
		-webkit-border-radius: 5px;
		border-radius: 5px;
		height: 50px;
		width: 100%;
}
</style>
