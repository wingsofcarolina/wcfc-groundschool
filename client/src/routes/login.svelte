<script>
	import { onMount } from 'svelte';
	import { goto } from '@sapper/app';

	onMount(function() {
	});
</script>

<svelte:head>
	<title>Login</title>
</svelte:head>

<div class=title>Authenticate</div>
<hr class="highlight">

<center>
<div class="narrow">
<p> Class content is intended to be available to only the registered/paid members
	of the ground school classes. Each member of the class needs to join the WCFC
	Groundschool Slack workspace. Contact <a href="mailto:cfi@wingsofcarolina.org">George
	Scheer</a> or <a href="mailto:bookkeeper@wingsofcarolina.org">Sue Davis</a>
	if you need an invitation to the workspace, or if you have any questions.
</p>

<p>If you are a WCFC Groundschool Slack workspace member, simply click on the button
	below to authenticate. This is intended to be a one-time operation, but if for
	some reason you clear the cookies in your browser you may have to re-authenticate
	since your credentials are stored in a browser cookie.
</p>

<div class="auth">
	<a href="https://slack.com/oauth/v2/authorize?user_scope=identity.basic,identity.email&client_id=REDACTED">
		<img alt="Sign in with Slack" height="40" width="172" src="https://platform.slack-edge.com/img/sign_in_with_slack.png"
		 srcset="https://platform.slack-edge.com/img/sign_in_with_slack.png 1x, https://platform.slack-edge.com/img/sign_in_with_slack@2x.png 2x" />
	</a>
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
	font-size: 1.2em;
}
</style>
