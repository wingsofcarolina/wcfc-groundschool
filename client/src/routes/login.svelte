<script>
	import { onMount } from 'svelte';
	import { goto } from '@sapper/app';
	import { notifier } from '@beyonk/svelte-notifications'

	let email = null;

	onMount(function() {
	});

	const sendMessage = async () => {
		if (email == null || email === "") {
			notifier.danger('Email address missing, but required.');
		}
		const response = await fetch('/api/email/' + email, {
			method: "get",
			withCredentials: true,
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json'
			}
		});

		if (!response.ok) {
			notifier.warning('Sending of request failed, contact class instructor(s) for help.');
		} else {
			notifier.success('Authentication request sent successfully.');
			email = null;
		}
		goto('/');
	}

</script>

<svelte:head>
	<title>Login</title>
</svelte:head>

<div class=title>Authenticate</div>
<hr class="highlight">

<center>
	<div class="narrow">

		<p> Class content is intended to be available to only the registered/paid
		members of the ground school classes. Contact
		<a href="mailto:cfi@wingsofcarolina.org">George Scheer</a> or
		<a href="mailto:bookkeeper@wingsofcarolina.org">Sue Davis</a> if you have need
		to register, or if you have any questions. </p>

		<p>If you <i><b>are</b></i> already registered, simply enter the email
		address you provided during registration in the field  below and click the
		"Submit Login" button. If your email address is found in the system an email
		will be sent to the registered address. That email will have a URL which
		will return you to the system with your authentication browser cookie set.
		his is intended to be a one-time operation, but if for some reason you clear
		the cookies in your browser you may have to re-authenticate since your
		credentials are stored in a browser cookie. </p>

	</div>

	<div class="section">
		<div class="contact_block">
			<div class="contact_info">
				<div class="contact_row">
					<input type="text" id="email" name="email" placeholder="Email"
					size=40 bind:value={email}>
				</div>
				<input id="submit" type="submit" value="Submit Login" on:click={() => sendMessage()}>
			</div>
		</div>
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
