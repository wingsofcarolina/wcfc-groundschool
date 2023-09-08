<script>
	import { onMount } from 'svelte';
	import { goto } from '@sapper/app';

	onMount(function() {
	});
</script>

<svelte:head>
	<title>Login</title>
</svelte:head>

<div class=title>Login Fail</div>
<hr class="highlight">

<center>
	<div class="narrow">

		<p> You have attempted to authenticate with the WCFC Groundschool
			server and have encountered a failure. If you waited too long to use
			your validation URL your code may have timed out. If that is the case
			try authenticating again, otherwise please contact the server
			administrators via the website <a href="contact">contact</a> page
			for further assistance.
		</p>
		<p> If you <i>clicked on the verification URL a second time</i> please
			re-read the instructions given. It is a <i>one use only</i> URL and
			once used is no longer valid.</p>
		<p> We apologize for the inconvenience. </p>
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
.narrow p {
	width: 70%;
	text-align: left;
}
</style>
